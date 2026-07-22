package dev.papershelf.domain.repository

import dev.papershelf.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun observeBookmarks(bookId: Long): Flow<List<Bookmark>>
}
