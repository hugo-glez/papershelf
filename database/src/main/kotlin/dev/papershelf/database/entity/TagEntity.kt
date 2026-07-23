package dev.papershelf.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tags",
    indices = [
        Index(value = ["normalizedName"], unique = true),
        Index(value = ["name"]),
    ],
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val normalizedName: String,
    val createdEpochMillis: Long,
)
