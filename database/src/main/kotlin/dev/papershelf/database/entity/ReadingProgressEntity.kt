package dev.papershelf.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reading_progress",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ReadingProgressEntity(
    @PrimaryKey
    val bookId: Long,
    val lastPage: Int?,
    val chapter: String?,
    val percentRead: Float,
    val readingTimeMillis: Long,
    val updatedEpochMillis: Long,
)
