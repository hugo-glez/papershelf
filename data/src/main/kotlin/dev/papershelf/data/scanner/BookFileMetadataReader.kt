package dev.papershelf.data.scanner

import dev.papershelf.database.entity.BookFormatEntity
import java.io.File
import javax.inject.Inject

class BookFileMetadataReader @Inject constructor(
    private val thumbnailGenerator: ThumbnailGenerator,
) {
    fun read(file: File): ScannedBookFile? {
        val format = file.bookFormat() ?: return null
        val epubMetadata = if (format == BookFormatEntity.Epub) {
            EpubMetadataReader().read(file)
        } else {
            null
        }
        return ScannedBookFile(
            title = epubMetadata?.title?.ifBlank { null } ?: file.cleanTitle(),
            author = epubMetadata?.author?.ifBlank { null },
            format = format,
            path = file.absolutePath,
            fileName = file.name,
            fileSizeBytes = file.length(),
            thumbnailPath = thumbnailGenerator.thumbnailFor(file, format),
            lastModifiedEpochMillis = file.lastModified(),
        )
    }

    private fun File.bookFormat(): BookFormatEntity? =
        when (extension.lowercase()) {
            "pdf" -> BookFormatEntity.Pdf
            "epub" -> BookFormatEntity.Epub
            else -> null
        }

    private fun File.cleanTitle(): String =
        nameWithoutExtension
            .replace(Regex("""[\._]+"""), " ")
            .replace(Regex("""\s*[-–—]\s*"""), " - ")
            .replace(Regex("""\[[^\]]*]|\([^)]*\)"""), " ")
            .replace(Regex("""\b(epub|pdf|ebook|scan|retail|spanish|espanol|english)\b""", RegexOption.IGNORE_CASE), " ")
            .replace(Regex("""\s+"""), " ")
            .trim(' ', '-', '_')
            .ifBlank { nameWithoutExtension.ifBlank { name } }
}
