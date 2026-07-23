package dev.papershelf.domain.repository

import dev.papershelf.domain.model.ReadingProgress
import kotlinx.coroutines.flow.Flow

interface ReadingProgressRepository {
    fun observeProgress(bookId: Long): Flow<ReadingProgress?>

    suspend fun saveProgress(
        bookId: Long,
        lastPage: Int?,
        chapter: String?,
        percentRead: Float,
        readingTimeMillis: Long = 0,
    )
}
