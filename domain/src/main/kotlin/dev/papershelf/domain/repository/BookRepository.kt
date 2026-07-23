package dev.papershelf.domain.repository

import dev.papershelf.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun observeBooks(): Flow<List<Book>>

    suspend fun updateFavorite(bookId: Long, isFavorite: Boolean)
}
