package dev.papershelf.database.dao

import androidx.room.Dao
import androidx.room.Query
import dev.papershelf.database.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    fun observeProgress(bookId: Long): Flow<ReadingProgressEntity?>
}
