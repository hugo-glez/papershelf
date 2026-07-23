package dev.papershelf.data.scanner

import androidx.room.withTransaction
import dev.papershelf.core.architecture.DispatcherProvider
import dev.papershelf.database.PaperShelfDatabase
import dev.papershelf.database.dao.BookDao
import dev.papershelf.database.entity.BookEntity
import dev.papershelf.domain.scanner.LibraryScanResult
import dev.papershelf.domain.scanner.LibraryScanner
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.withContext

@Singleton
class FileSystemLibraryScanner @Inject constructor(
    private val database: PaperShelfDatabase,
    private val bookDao: BookDao,
    private val metadataReader: BookFileMetadataReader,
    private val dispatchers: DispatcherProvider,
) : LibraryScanner {
    override suspend fun scan(folderPath: String): LibraryScanResult =
        withContext(dispatchers.io) {
            val root = File(folderPath)
            val scannedAt = System.currentTimeMillis()
            val scannedFiles = root.scanBookFiles()
            val scannedPaths = scannedFiles.mapTo(mutableSetOf()) { it.path }

            var added = 0
            var updated = 0
            var unavailable = 0

            database.withTransaction {
                val existingBooks = bookDao.getBooks()
                val existingByPath = existingBooks.associateBy { it.path }

                scannedFiles.forEach { scannedFile ->
                    val existing = existingByPath[scannedFile.path]
                    if (existing == null) {
                        bookDao.insertBook(scannedFile.toEntity(scannedAt))
                        added += 1
                    } else {
                        val refreshed = existing.refreshFrom(scannedFile, scannedAt)
                        if (refreshed != existing) {
                            bookDao.updateBook(refreshed)
                            updated += 1
                        }
                    }
                }

                val missingPaths = existingBooks
                    .asSequence()
                    .filter { it.isAvailable }
                    .filter { it.path.isUnder(root) }
                    .filterNot { it.path in scannedPaths }
                    .map { it.path }
                    .toList()

                if (missingPaths.isNotEmpty()) {
                    bookDao.markUnavailable(missingPaths, scannedAt)
                    unavailable = missingPaths.size
                }
            }

            LibraryScanResult(
                scannedFolderPath = root.absolutePath,
                filesFound = scannedFiles.size,
                booksAdded = added,
                booksUpdated = updated,
                booksMarkedUnavailable = unavailable,
            )
        }

    private fun File.scanBookFiles(): List<ScannedBookFile> {
        if (!exists() || !isDirectory) return emptyList()

        return walkTopDown()
            .onEnter { directory -> !directory.isHidden }
            .filter { file -> file.isFile && !file.isHidden }
            .mapNotNull { file -> metadataReader.read(file) }
            .toList()
    }

    private fun ScannedBookFile.toEntity(scannedAt: Long): BookEntity =
        BookEntity(
            title = title,
            author = author,
            format = format,
            path = path,
            fileName = fileName,
            fileSizeBytes = fileSizeBytes,
            pageCount = null,
            thumbnailPath = null,
            lastModifiedEpochMillis = lastModifiedEpochMillis,
            dateAddedEpochMillis = scannedAt,
            lastScannedEpochMillis = scannedAt,
            lastReadEpochMillis = null,
            progressPercent = 0f,
            isFavorite = false,
            isAvailable = true,
        )

    private fun BookEntity.refreshFrom(
        scannedFile: ScannedBookFile,
        scannedAt: Long,
    ): BookEntity =
        copy(
            title = scannedFile.title,
            author = author ?: scannedFile.author,
            format = scannedFile.format,
            fileName = scannedFile.fileName,
            fileSizeBytes = scannedFile.fileSizeBytes,
            lastModifiedEpochMillis = scannedFile.lastModifiedEpochMillis,
            lastScannedEpochMillis = scannedAt,
            isAvailable = true,
        )

    private fun String.isUnder(root: File): Boolean {
        val rootPath = root.absolutePath.trimEnd(File.separatorChar)
        return this == rootPath || startsWith(rootPath + File.separator)
    }
}
