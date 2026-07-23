package dev.papershelf.data.scanner

import dev.papershelf.database.entity.BookFormatEntity

data class ScannedBookFile(
    val title: String,
    val author: String?,
    val format: BookFormatEntity,
    val path: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val lastModifiedEpochMillis: Long,
)
