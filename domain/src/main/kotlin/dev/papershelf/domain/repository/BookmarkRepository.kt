package dev.papershelf.domain.repository

import dev.papershelf.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun observeBookmarks(bookId: Long): Flow<List<Bookmark>>

    suspend fun addBookmark(
        bookId: Long,
        page: Int?,
        chapter: String?,
        note: String? = null,
    ): Long

    suspend fun deleteBookmark(bookmark: Bookmark)
}
