package dev.papershelf.data.repository

import dev.papershelf.data.mapper.toDomain
import dev.papershelf.database.dao.BookDao
import dev.papershelf.domain.model.Book
import dev.papershelf.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
) : BookRepository {
    override fun observeBooks(): Flow<List<Book>> =
        bookDao.observeBooks().map { books -> books.map { it.toDomain() } }
}
