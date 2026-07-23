package dev.papershelf.reader.pdf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberPdfReaderLauncher(): PdfReaderLauncher {
    val context = LocalContext.current
    return remember(context) { PdfReaderLauncher(context) }
}
