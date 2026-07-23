package dev.papershelf.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.papershelf.database.entity.BookTagEntity
import dev.papershelf.database.entity.TagEntity
import dev.papershelf.database.relation.BookTagProjection
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY name COLLATE NOCASE ASC")
    fun observeTags(): Flow<List<TagEntity>>

    @Query(
        """
        SELECT book_tags.bookId AS bookId, tags.id AS tagId, tags.name AS name
        FROM book_tags
        INNER JOIN tags ON tags.id = book_tags.tagId
        ORDER BY tags.name COLLATE NOCASE ASC
        """,
    )
    fun observeBookTags(): Flow<List<BookTagProjection>>

    @Query("SELECT * FROM tags WHERE normalizedName = :normalizedName LIMIT 1")
    suspend fun getTagByNormalizedName(normalizedName: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookTag(bookTag: BookTagEntity)

    @Query("DELETE FROM book_tags WHERE bookId = :bookId AND tagId = :tagId")
    suspend fun deleteBookTag(bookId: Long, tagId: Long)

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTag(tagId: Long)

    @Query("DELETE FROM book_tags")
    suspend fun deleteAllBookTags()

    @Query("DELETE FROM tags")
    suspend fun deleteAllTags()
}
