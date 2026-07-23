package dev.papershelf.data.repository

import dev.papershelf.data.mapper.toDomain
import dev.papershelf.data.mapper.toEntity
import dev.papershelf.database.dao.SettingsDao
import dev.papershelf.domain.model.AppSettings
import dev.papershelf.domain.model.ThemeMode
import dev.papershelf.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao,
) : SettingsRepository {
    override fun observeSettings(): Flow<AppSettings> =
        settingsDao.observeSettings().map { it?.toDomain() ?: AppSettings.Default }

    override suspend fun updateBooksFolder(path: String) {
        update { it.copy(booksFolderPath = path.ifBlank { AppSettings.Default.booksFolderPath }) }
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        update { it.copy(themeMode = themeMode) }
    }

    override suspend fun updateAnimationsEnabled(enabled: Boolean) {
        update { it.copy(animationsEnabled = enabled) }
    }

    override suspend fun updateThumbnailSizeDp(sizeDp: Int) {
        update { it.copy(thumbnailSizeDp = sizeDp.coerceIn(48, 160)) }
    }

    private suspend fun update(transform: (AppSettings) -> AppSettings) {
        val current = settingsDao.getSettings()?.toDomain() ?: AppSettings.Default
        settingsDao.upsertSettings(transform(current).toEntity())
    }
}
