package dev.papershelf.data.scanner

import dev.papershelf.database.entity.BookFormatEntity
import java.io.File
import javax.inject.Inject

class BookFileMetadataReader @Inject constructor() {
    fun read(file: File): ScannedBookFile? {
        val format = file.bookFormat() ?: return null
        return ScannedBookFile(
            title = file.nameWithoutExtension.ifBlank { file.name },
            author = null,
            format = format,
            path = file.absolutePath,
            fileName = file.name,
            fileSizeBytes = file.length(),
            lastModifiedEpochMillis = file.lastModified(),
        )
    }

    private fun File.bookFormat(): BookFormatEntity? =
        when (extension.lowercase()) {
            "pdf" -> BookFormatEntity.Pdf
            "epub" -> BookFormatEntity.Epub
            else -> null
        }
}
