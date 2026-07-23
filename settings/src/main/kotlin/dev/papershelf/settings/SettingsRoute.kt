package dev.papershelf.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.papershelf.domain.model.AppSettings
import dev.papershelf.domain.model.ThemeMode
import kotlin.math.roundToInt

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onFolderChange = viewModel::updateBooksFolder,
        onThemeChange = viewModel::updateThemeMode,
        onAnimationsChange = viewModel::updateAnimationsEnabled,
        onThumbnailSizeChange = viewModel::updateThumbnailSizeDp,
    )
}

@Composable
fun SettingsScreen(
    uiState: AppSettings,
    onFolderChange: (String) -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    onAnimationsChange: (Boolean) -> Unit,
    onThumbnailSizeChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Configuracion",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Biblioteca",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                OutlinedTextField(
                    value = uiState.booksFolderPath,
                    onValueChange = onFolderChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Carpeta de libros") },
                )
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Tema",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { themeMode ->
                        FilterChip(
                            selected = uiState.themeMode == themeMode,
                            onClick = { onThemeChange(themeMode) },
                            label = { Text(themeMode.label) },
                        )
                    }
                }
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = "Animaciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = if (uiState.animationsEnabled) "Activadas" else "Desactivadas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = uiState.animationsEnabled,
                        onCheckedChange = onAnimationsChange,
                    )
                }

                Text(
                    text = "Tamaño miniaturas: ${uiState.thumbnailSizeDp} dp",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                Slider(
                    value = uiState.thumbnailSizeDp.toFloat(),
                    onValueChange = { onThumbnailSizeChange(it.roundToInt()) },
                    valueRange = 48f..160f,
                    steps = 6,
                )
            }
        }
    }
}

private val ThemeMode.label: String
    get() = when (this) {
        ThemeMode.Light -> "Claro"
        ThemeMode.Dark -> "Oscuro"
        ThemeMode.System -> "Sistema"
    }
