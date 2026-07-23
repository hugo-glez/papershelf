package dev.papershelf.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.papershelf.domain.model.Book
import dev.papershelf.domain.model.BookFormat
import java.text.DateFormat
import java.util.Date

@Composable
fun LibraryRoute(
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LibraryScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onFilterChange = viewModel::onFilterChange,
        onSortChange = viewModel::onSortChange,
        onScanClick = viewModel::scanLibrary,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onQueryChange: (String) -> Unit,
    onFilterChange: (LibraryFilter) -> Unit,
    onSortChange: (LibrarySort) -> Unit,
    onScanClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            LibraryTopBar(
                uiState = uiState,
                onScanClick = onScanClick,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LibraryControls(
                uiState = uiState,
                onQueryChange = onQueryChange,
                onFilterChange = onFilterChange,
                onSortChange = onSortChange,
            )

            ScanMessage(uiState)

            if (uiState.books.isEmpty()) {
                EmptyLibraryState(
                    hasQuery = uiState.query.isNotBlank() || uiState.filter != LibraryFilter.All,
                    onScanClick = onScanClick,
                    isScanning = uiState.isScanning,
                    modifier = Modifier.weight(1f),
                )
            } else {
                BookList(
                    books = uiState.books,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun LibraryTopBar(
    uiState: LibraryUiState,
    onScanClick: () -> Unit,
) {
    Surface(
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PaperShelf",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${uiState.totalBooks} libros  |  ${uiState.pdfBooks} PDF  |  ${uiState.epubBooks} EPUB",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            TooltipIconButton(
                tooltip = "Escanear biblioteca",
                enabled = !uiState.isScanning,
                onClick = onScanClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun LibraryControls(
    uiState: LibraryUiState,
    onQueryChange: (String) -> Unit,
    onFilterChange: (LibraryFilter) -> Unit,
    onSortChange: (LibrarySort) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                    )
                },
                placeholder = { Text("Buscar titulo, autor o archivo") },
            )

            SortMenu(
                selectedSort = uiState.sort,
                onSortChange = onSortChange,
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LibraryFilter.entries.forEach { filter ->
                FilterChip(
                    selected = uiState.filter == filter,
                    onClick = { onFilterChange(filter) },
                    label = { Text(filter.label) },
                )
            }
        }
    }
}

@Composable
private fun SortMenu(
    selectedSort: LibrarySort,
    onSortChange: (LibrarySort) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TooltipIconButton(
            tooltip = "Ordenar por ${selectedSort.label}",
            onClick = { expanded = true },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Sort,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            LibrarySort.entries.forEach { sort ->
                DropdownMenuItem(
                    text = { Text(sort.label) },
                    onClick = {
                        expanded = false
                        onSortChange(sort)
                    },
                )
            }
        }
    }
}

@Composable
private fun ScanMessage(uiState: LibraryUiState) {
    val result = uiState.lastScanResult
    when {
        uiState.isScanning -> StatusRow(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                )
            },
            text = "Escaneando biblioteca...",
        )

        uiState.scanError != null -> StatusRow(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            text = uiState.scanError,
        )

        result != null -> StatusRow(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                )
            },
            text = "${result.filesFound} archivos, ${result.booksAdded} nuevos, ${result.booksUpdated} actualizados, ${result.booksMarkedUnavailable} no disponibles",
        )
    }
}

@Composable
private fun StatusRow(
    icon: @Composable () -> Unit,
    text: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.size(18.dp)) {
            icon()
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun BookList(
    books: List<Book>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = books,
            key = { it.id },
        ) { book ->
            BookRow(book)
        }
    }
}

@Composable
private fun BookRow(book: Book) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BookCoverPlaceholder(book)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = book.title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    if (book.isFavorite) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    if (!book.isAvailable) {
                        Icon(
                            imageVector = Icons.Outlined.Block,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                Text(
                    text = book.author ?: book.fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(book.format.label) },
                        leadingIcon = {
                            Icon(
                                imageVector = book.format.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        },
                    )

                    Text(
                        text = book.detailText(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                LinearProgressIndicator(
                    progress = { book.progressPercent.coerceIn(0f, 100f) / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp)),
                )
            }
        }
    }
}

@Composable
private fun BookCoverPlaceholder(book: Book) {
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 72.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = book.format.icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyLibraryState(
    hasQuery: Boolean,
    onScanClick: () -> Unit,
    isScanning: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.FolderOpen,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = if (hasQuery) "No hay libros con ese filtro" else "Biblioteca vacia",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = if (hasQuery) {
                "Ajusta la busqueda o cambia el filtro."
            } else {
                "Escanea /storage/emulated/0/Books para indexar PDF y EPUB."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (!hasQuery) {
            Button(
                enabled = !isScanning,
                onClick = onScanClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Escanear")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TooltipIconButton(
    tooltip: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { Text(tooltip) },
        state = rememberTooltipState(),
    ) {
        IconButton(
            enabled = enabled,
            onClick = onClick,
        ) {
            icon()
        }
    }
}

private val BookFormat.label: String
    get() = when (this) {
        BookFormat.Pdf -> "PDF"
        BookFormat.Epub -> "EPUB"
    }

private val BookFormat.icon
    get() = when (this) {
        BookFormat.Pdf -> Icons.Outlined.PictureAsPdf
        BookFormat.Epub -> Icons.AutoMirrored.Outlined.MenuBook
    }

private fun Book.detailText(): String {
    val progress = "${progressPercent.toInt().coerceIn(0, 100)}%"
    val pages = pageCount?.let { "$it paginas" }
    val lastRead = lastReadEpochMillis?.let { "Leido ${DateFormat.getDateInstance().format(Date(it))}" }
    return listOfNotNull(progress, pages, lastRead, fileSizeBytes.formatBytes())
        .joinToString("  |  ")
}

private fun Long.formatBytes(): String =
    when {
        this >= 1_000_000_000 -> "%.1f GB".format(this / 1_000_000_000.0)
        this >= 1_000_000 -> "%.1f MB".format(this / 1_000_000.0)
        this >= 1_000 -> "%.1f KB".format(this / 1_000.0)
        else -> "$this B"
    }
