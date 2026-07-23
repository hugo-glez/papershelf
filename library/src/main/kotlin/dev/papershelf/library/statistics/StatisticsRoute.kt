package dev.papershelf.library.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun StatisticsRoute(
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StatisticsScreen(uiState)
}

@Composable
fun StatisticsScreen(uiState: StatisticsUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Estadisticas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard("Libros", uiState.totalBooks.toString(), Icons.Outlined.Book)
            StatCard("PDF", uiState.pdfBooks.toString(), Icons.Outlined.PictureAsPdf)
            StatCard("EPUB", uiState.epubBooks.toString(), Icons.AutoMirrored.Outlined.MenuBook)
            StatCard("Favoritos", uiState.favorites.toString(), Icons.Outlined.FavoriteBorder)
            StatCard("Iniciados", uiState.startedBooks.toString(), Icons.Outlined.QueryStats)
            StatCard("Terminados", uiState.finishedBooks.toString(), Icons.Outlined.CheckCircle)
            StatCard("No iniciados", uiState.notStartedBooks.toString(), Icons.Outlined.Book)
            StatCard("No disponibles", uiState.unavailableBooks.toString(), Icons.Outlined.CloudOff)
            StatCard("Paginas conocidas", uiState.pagesKnown.toString(), Icons.AutoMirrored.Outlined.MenuBook)
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Progreso promedio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                LinearProgressIndicator(
                    progress = { uiState.averageProgress.coerceIn(0f, 100f) / 100f },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "${uiState.averageProgress.toInt().coerceIn(0, 100)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(0.31f),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
