package dev.papershelf.data.repository

import dev.papershelf.data.mapper.toDomain
import dev.papershelf.database.dao.BookmarkDao
import dev.papershelf.database.entity.BookmarkEntity
import dev.papershelf.domain.model.Bookmark
import dev.papershelf.domain.repository.BookmarkRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao,
) : BookmarkRepository {
    override fun observeBookmarks(bookId: Long): Flow<List<Bookmark>> =
        bookmarkDao.observeBookmarks(bookId).map { bookmarks ->
            bookmarks.map { it.toDomain() }
        }

    override suspend fun addBookmark(
        bookId: Long,
        page: Int?,
        chapter: String?,
        note: String?,
    ): Long =
        bookmarkDao.insertBookmark(
            BookmarkEntity(
                bookId = bookId,
                page = page,
                chapter = chapter,
                note = note,
                createdEpochMillis = System.currentTimeMillis(),
            ),
        )

    override suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.deleteBookmarkById(bookmark.id)
    }
}
