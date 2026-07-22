package dev.papershelf.database.dao

import androidx.room.Dao
import androidx.room.Query
import dev.papershelf.database.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun observeSettings(): Flow<SettingsEntity?>
}
