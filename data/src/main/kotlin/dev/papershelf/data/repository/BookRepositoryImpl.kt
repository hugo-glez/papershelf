package dev.papershelf.data.repository

import dev.papershelf.domain.model.Book
import dev.papershelf.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class BookRepositoryImpl : BookRepository {
    override fun observeBooks(): Flow<List<Book>> = emptyFlow()
}
