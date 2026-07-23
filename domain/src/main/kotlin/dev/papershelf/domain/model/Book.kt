package dev.papershelf.domain.model

data class Book(
    val id: Long,
    val title: String,
    val author: String?,
    val format: BookFormat,
    val path: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val pageCount: Int?,
    val thumbnailPath: String?,
    val dateAddedEpochMillis: Long,
    val lastReadEpochMillis: Long?,
    val progressPercent: Float,
    val isAvailable: Boolean,
    val isFavorite: Boolean,
)
