package dev.papershelf.data.repository

import dev.papershelf.data.mapper.toDomain
import dev.papershelf.database.dao.TagDao
import dev.papershelf.database.entity.BookTagEntity
import dev.papershelf.database.entity.TagEntity
import dev.papershelf.domain.model.BookTag
import dev.papershelf.domain.repository.TagRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
) : TagRepository {
    override fun observeTags(): Flow<List<BookTag>> =
        tagDao.observeTags().map { tags -> tags.map { it.toDomain() } }

    override fun observeBookTags(): Flow<Map<Long, List<BookTag>>> =
        tagDao.observeBookTags().map { projections ->
            projections
                .groupBy { it.bookId }
                .mapValues { (_, values) -> values.map { it.toDomain() } }
        }

    override suspend fun createTag(name: String): BookTag {
        val cleanedName = name.trim().replace(Regex("""\s+"""), " ")
        require(cleanedName.isNotBlank()) { "La etiqueta no puede estar vacia" }

        val normalizedName = cleanedName.normalizedTagName()
        tagDao.getTagByNormalizedName(normalizedName)?.let { return it.toDomain() }

        val now = System.currentTimeMillis()
        val id = tagDao.insertTag(
            TagEntity(
                name = cleanedName,
                normalizedName = normalizedName,
                createdEpochMillis = now,
            ),
        )
        return BookTag(
            id = id,
            name = cleanedName,
        )
    }

    override suspend fun addTagToBook(bookId: Long, tagId: Long) {
        tagDao.insertBookTag(BookTagEntity(bookId = bookId, tagId = tagId))
    }

    override suspend fun removeTagFromBook(bookId: Long, tagId: Long) {
        tagDao.deleteBookTag(bookId = bookId, tagId = tagId)
    }

    override suspend fun deleteTag(tagId: Long) {
        tagDao.deleteTag(tagId)
    }

    private fun String.normalizedTagName(): String =
        lowercase().trim().replace(Regex("""\s+"""), " ")
}
