package dev.papershelf.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import dev.papershelf.core.ui.PaperShelfTheme
import dev.papershelf.domain.model.AppSettings
import dev.papershelf.domain.model.ThemeMode
import dev.papershelf.domain.repository.SettingsRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by settingsRepository.observeSettings().collectAsState(AppSettings.Default)
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (settings.themeMode) {
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
                ThemeMode.System -> systemDark
            }

            PaperShelfTheme(darkTheme = darkTheme) {
                PaperShelfRoot()
            }
        }
    }
}
