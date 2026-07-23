package dev.papershelf.data.repository

import dev.papershelf.domain.model.Bookmark
import dev.papershelf.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor() : BookmarkRepository {
    override fun observeBookmarks(bookId: Long): Flow<List<Bookmark>> = emptyFlow()
}
