package dev.papershelf.domain.repository

import dev.papershelf.domain.model.BookTag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun observeTags(): Flow<List<BookTag>>

    fun observeBookTags(): Flow<Map<Long, List<BookTag>>>

    suspend fun createTag(name: String): BookTag

    suspend fun addTagToBook(bookId: Long, tagId: Long)

    suspend fun removeTagFromBook(bookId: Long, tagId: Long)

    suspend fun deleteTag(tagId: Long)
}
