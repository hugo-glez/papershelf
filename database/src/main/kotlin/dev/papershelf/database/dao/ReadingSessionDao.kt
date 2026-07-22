package dev.papershelf.database.dao

import androidx.room.Dao
import androidx.room.Query
import dev.papershelf.database.entity.ReadingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {
    @Query("SELECT * FROM reading_sessions WHERE bookId = :bookId")
    fun observeSessions(bookId: Long): Flow<List<ReadingSessionEntity>>
}
