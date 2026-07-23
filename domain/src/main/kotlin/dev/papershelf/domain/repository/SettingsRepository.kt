package dev.papershelf.domain.repository

import dev.papershelf.domain.model.AppSettings
import dev.papershelf.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>

    suspend fun updateBooksFolder(path: String)

    suspend fun updateThemeMode(themeMode: ThemeMode)

    suspend fun updateAnimationsEnabled(enabled: Boolean)

    suspend fun updateThumbnailSizeDp(sizeDp: Int)
}
