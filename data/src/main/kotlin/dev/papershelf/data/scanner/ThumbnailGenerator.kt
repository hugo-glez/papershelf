package dev.papershelf.data.scanner

import dev.papershelf.database.entity.BookFormatEntity
import java.io.File

interface ThumbnailGenerator {
    fun thumbnailFor(
        file: File,
        format: BookFormatEntity,
    ): String?
}
