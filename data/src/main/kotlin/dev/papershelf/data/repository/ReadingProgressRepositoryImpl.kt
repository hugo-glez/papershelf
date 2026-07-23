package dev.papershelf.data.repository

import androidx.room.withTransaction
import dev.papershelf.data.mapper.toDomain
import dev.papershelf.database.PaperShelfDatabase
import dev.papershelf.database.dao.BookDao
import dev.papershelf.database.dao.ReadingProgressDao
import dev.papershelf.database.entity.ReadingProgressEntity
import dev.papershelf.domain.model.ReadingProgress
import dev.papershelf.domain.progress.ReadingProgressCalculator
import dev.papershelf.domain.repository.ReadingProgressRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReadingProgressRepositoryImpl @Inject constructor(
    private val database: PaperShelfDatabase,
    private val readingProgressDao: ReadingProgressDao,
    private val bookDao: BookDao,
) : ReadingProgressRepository {
    override fun observeProgress(bookId: Long): Flow<ReadingProgress?> =
        readingProgressDao.observeProgress(bookId).map { it?.toDomain() }

    override suspend fun saveProgress(
        bookId: Long,
        lastPage: Int?,
        chapter: String?,
        percentRead: Float,
        readingTimeMillis: Long,
    ) {
        val now = System.currentTimeMillis()
        val boundedPercent = ReadingProgressCalculator.bound(percentRead)
        database.withTransaction {
            readingProgressDao.upsertProgress(
                ReadingProgressEntity(
                    bookId = bookId,
                    lastPage = lastPage,
                    chapter = chapter,
                    percentRead = boundedPercent,
                    readingTimeMillis = readingTimeMillis,
                    updatedEpochMillis = now,
                ),
            )
            bookDao.updateReadingSummary(
                bookId = bookId,
                progressPercent = boundedPercent,
                lastReadEpochMillis = now,
            )
        }
    }
}
