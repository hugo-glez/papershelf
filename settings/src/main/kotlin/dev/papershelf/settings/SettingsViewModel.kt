package dev.papershelf.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.papershelf.domain.model.AppSettings
import dev.papershelf.domain.model.ThemeMode
import dev.papershelf.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val uiState: StateFlow<AppSettings> =
        settingsRepository.observeSettings()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppSettings.Default,
            )

    fun updateBooksFolder(path: String) {
        viewModelScope.launch { settingsRepository.updateBooksFolder(path) }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch { settingsRepository.updateThemeMode(themeMode) }
    }

    fun updateAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateAnimationsEnabled(enabled) }
    }

    fun updateThumbnailSizeDp(sizeDp: Int) {
        viewModelScope.launch { settingsRepository.updateThumbnailSizeDp(sizeDp) }
    }
}
