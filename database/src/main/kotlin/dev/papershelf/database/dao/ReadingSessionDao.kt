package dev.papershelf.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.papershelf.database.entity.ReadingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {
    @Query("SELECT * FROM reading_sessions WHERE bookId = :bookId")
    fun observeSessions(bookId: Long): Flow<List<ReadingSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSession(session: ReadingSessionEntity): Long

    @Query("DELETE FROM reading_sessions")
    suspend fun deleteAllSessions()
}
