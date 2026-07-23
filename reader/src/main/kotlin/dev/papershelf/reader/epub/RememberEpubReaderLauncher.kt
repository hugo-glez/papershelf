package dev.papershelf.reader.epub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberEpubReaderLauncher(): EpubReaderLauncher {
    val context = LocalContext.current
    return remember(context) { EpubReaderLauncher(context) }
}
