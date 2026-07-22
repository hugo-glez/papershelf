package dev.papershelf.domain.model

data class ReadingProgress(
    val bookId: Long,
    val lastPage: Int?,
    val percentRead: Float,
    val readingTimeMillis: Long,
)
