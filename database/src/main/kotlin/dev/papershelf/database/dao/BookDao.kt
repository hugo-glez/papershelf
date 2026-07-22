package dev.papershelf.database.dao

import androidx.room.Dao
import androidx.room.Query
import dev.papershelf.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun observeBooks(): Flow<List<BookEntity>>
}
