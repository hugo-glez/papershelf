package dev.papershelf.data.repository

import androidx.room.withTransaction
import dev.papershelf.database.PaperShelfDatabase
import dev.papershelf.database.dao.BookDao
import dev.papershelf.database.dao.BookmarkDao
import dev.papershelf.database.dao.ReadingProgressDao
import dev.papershelf.database.dao.ReadingSessionDao
import dev.papershelf.database.dao.TagDao
import dev.papershelf.domain.repository.LibraryMaintenanceRepository
import javax.inject.Inject

class LibraryMaintenanceRepositoryImpl @Inject constructor(
    private val database: PaperShelfDatabase,
    private val bookDao: BookDao,
    private val bookmarkDao: BookmarkDao,
    private val readingProgressDao: ReadingProgressDao,
    private val readingSessionDao: ReadingSessionDao,
    private val tagDao: TagDao,
) : LibraryMaintenanceRepository {
    override suspend fun clearLibraryData() {
        database.withTransaction {
            bookmarkDao.deleteAllBookmarks()
            readingProgressDao.deleteAllProgress()
            readingSessionDao.deleteAllSessions()
            tagDao.deleteAllBookTags()
            tagDao.deleteAllTags()
            bookDao.deleteAllBooks()
        }
    }
}
