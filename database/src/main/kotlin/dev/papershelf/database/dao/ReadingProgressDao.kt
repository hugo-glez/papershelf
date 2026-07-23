package dev.papershelf.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.papershelf.database.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    fun observeProgress(bookId: Long): Flow<ReadingProgressEntity?>

    @Upsert
    suspend fun upsertProgress(progress: ReadingProgressEntity)

    @Query("DELETE FROM reading_progress")
    suspend fun deleteAllProgress()
}
