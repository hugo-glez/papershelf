package dev.papershelf.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.papershelf.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title COLLATE NOCASE ASC")
    fun observeBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books")
    suspend fun getBooks(): List<BookEntity>

    @Query("SELECT * FROM books WHERE path = :path LIMIT 1")
    suspend fun getBookByPath(path: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBook(book: BookEntity): Long

    @Update
    suspend fun updateBook(book: BookEntity)

    @Query("UPDATE books SET isFavorite = :isFavorite WHERE id = :bookId")
    suspend fun updateFavorite(bookId: Long, isFavorite: Boolean)

    @Query(
        """
        UPDATE books
        SET isAvailable = 0, lastScannedEpochMillis = :scannedAtEpochMillis
        WHERE path IN (:paths)
        """,
    )
    suspend fun markUnavailable(paths: List<String>, scannedAtEpochMillis: Long)

    @Query("SELECT COUNT(*) FROM books")
    suspend fun countBooks(): Int

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

    @Query(
        """
        UPDATE books
        SET progressPercent = :progressPercent,
            lastReadEpochMillis = :lastReadEpochMillis,
            pageCount = COALESCE(:pageCount, pageCount)
        WHERE id = :bookId
        """,
    )
    suspend fun updateReadingSummary(
        bookId: Long,
        progressPercent: Float,
        lastReadEpochMillis: Long,
        pageCount: Int?,
    )
}
