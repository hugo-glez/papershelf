package dev.papershelf.data.mapper

import dev.papershelf.database.entity.SettingsEntity
import dev.papershelf.domain.model.AppSettings
import dev.papershelf.domain.model.ThemeMode

fun SettingsEntity.toDomain(): AppSettings =
    AppSettings(
        booksFolderPath = booksFolderPath,
        themeMode = runCatching { ThemeMode.valueOf(themeMode) }.getOrDefault(ThemeMode.System),
        animationsEnabled = animationsEnabled,
        thumbnailSizeDp = thumbnailSizeDp,
    )

fun AppSettings.toEntity(): SettingsEntity =
    SettingsEntity(
        booksFolderPath = booksFolderPath,
        themeMode = themeMode.name,
        animationsEnabled = animationsEnabled,
        thumbnailSizeDp = thumbnailSizeDp,
    )
