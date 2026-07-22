package dev.papershelf.database.dao

import androidx.room.Dao
import androidx.room.Query
import dev.papershelf.database.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId")
    fun observeBookmarks(bookId: Long): Flow<List<BookmarkEntity>>
}
