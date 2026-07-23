package dev.papershelf.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["path"], unique = true),
        Index(value = ["title"]),
        Index(value = ["author"]),
    ],
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String?,
    val format: BookFormatEntity,
    val path: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val pageCount: Int?,
    val thumbnailPath: String?,
    val lastModifiedEpochMillis: Long,
    val dateAddedEpochMillis: Long,
    val lastScannedEpochMillis: Long,
    val lastReadEpochMillis: Long?,
    val progressPercent: Float,
    val isFavorite: Boolean,
    val isAvailable: Boolean,
)
