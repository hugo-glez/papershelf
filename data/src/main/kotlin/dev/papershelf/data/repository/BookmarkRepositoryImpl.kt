package dev.papershelf.data.repository

import dev.papershelf.domain.model.Bookmark
import dev.papershelf.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class BookmarkRepositoryImpl : BookmarkRepository {
    override fun observeBookmarks(bookId: Long): Flow<List<Bookmark>> = emptyFlow()
}
