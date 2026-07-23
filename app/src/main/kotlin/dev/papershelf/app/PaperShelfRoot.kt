package dev.papershelf.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.papershelf.domain.model.BookFormat
import dev.papershelf.library.LibraryRoute
import dev.papershelf.library.statistics.StatisticsRoute
import dev.papershelf.reader.epub.rememberEpubReaderLauncher
import dev.papershelf.reader.pdf.rememberPdfReaderLauncher
import dev.papershelf.settings.SettingsRoute

@Composable
fun PaperShelfRoot() {
    val pdfReaderLauncher = rememberPdfReaderLauncher()
    val epubReaderLauncher = rememberEpubReaderLauncher()
    var selectedTab by remember { mutableStateOf(PaperShelfTab.Library) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                PaperShelfTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                            )
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
        ) {
            when (selectedTab) {
                PaperShelfTab.Library -> LibraryRoute(
                    onOpenBook = { book ->
                        when (book.format) {
                            BookFormat.Pdf -> pdfReaderLauncher.open(
                                bookId = book.id,
                                path = book.path,
                                pageCount = book.pageCount,
                            )
                            BookFormat.Epub -> epubReaderLauncher.open(
                                bookId = book.id,
                                path = book.path,
                                pageCount = book.pageCount,
                            )
                        }
                    },
                )

                PaperShelfTab.Statistics -> StatisticsRoute()
                PaperShelfTab.Settings -> SettingsRoute()
            }
        }
    }
}

private enum class PaperShelfTab(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Library("Biblioteca", Icons.AutoMirrored.Outlined.LibraryBooks),
    Statistics("Estadisticas", Icons.Outlined.QueryStats),
    Settings("Ajustes", Icons.Outlined.Settings),
}
