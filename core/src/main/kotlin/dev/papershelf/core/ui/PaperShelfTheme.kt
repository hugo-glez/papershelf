package dev.papershelf.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2F5F5A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD4E8E3),
    onPrimaryContainer = Color(0xFF0D1F1D),
    secondary = Color(0xFF6A5D3F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF3E4BC),
    onSecondaryContainer = Color(0xFF241A05),
    tertiary = Color(0xFF8A4A3C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDAD2),
    onTertiaryContainer = Color(0xFF35100A),
    background = Color(0xFFFAFCF8),
    onBackground = Color(0xFF191C1B),
    surface = Color(0xFFFAFCF8),
    onSurface = Color(0xFF191C1B),
    surfaceVariant = Color(0xFFDDE5E1),
    onSurfaceVariant = Color(0xFF414946),
    outline = Color(0xFF717975),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB8CCC7),
    onPrimary = Color(0xFF213A37),
    primaryContainer = Color(0xFF38514E),
    onPrimaryContainer = Color(0xFFD4E8E3),
    secondary = Color(0xFFD6C8A1),
    onSecondary = Color(0xFF3A2F15),
    secondaryContainer = Color(0xFF51462A),
    onSecondaryContainer = Color(0xFFF3E4BC),
    tertiary = Color(0xFFFFB4A5),
    onTertiary = Color(0xFF532014),
    tertiaryContainer = Color(0xFF6F3328),
    onTertiaryContainer = Color(0xFFFFDAD2),
    background = Color(0xFF101413),
    onBackground = Color(0xFFE0E3E0),
    surface = Color(0xFF101413),
    onSurface = Color(0xFFE0E3E0),
    surfaceVariant = Color(0xFF414946),
    onSurfaceVariant = Color(0xFFC1C9C5),
    outline = Color(0xFF8B938F),
)

@Composable
fun PaperShelfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
