package dev.papershelf.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val booksFolderPath: String,
    val themeMode: String,
    val animationsEnabled: Boolean,
    val thumbnailSizeDp: Int,
)
