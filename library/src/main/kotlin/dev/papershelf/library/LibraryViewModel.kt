package dev.papershelf.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.papershelf.domain.model.Book
import dev.papershelf.domain.model.BookFormat
import dev.papershelf.domain.repository.BookRepository
import dev.papershelf.domain.scanner.LibraryScanResult
import dev.papershelf.domain.scanner.LibraryScanner
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryViewModel @Inject constructor(
    bookRepository: BookRepository,
    private val libraryScanner: LibraryScanner,
) : ViewModel() {
    private val controls = MutableStateFlow(LibraryControls())
    private val scanState = MutableStateFlow(LibraryScanState())

    val uiState: StateFlow<LibraryUiState> =
        combine(
            bookRepository.observeBooks(),
            controls,
            scanState,
        ) { books, controls, scanState ->
            LibraryUiState(
                books = books.toVisibleBooks(controls),
                totalBooks = books.size,
                pdfBooks = books.count { it.format == BookFormat.Pdf },
                epubBooks = books.count { it.format == BookFormat.Epub },
                query = controls.query,
                filter = controls.filter,
                sort = controls.sort,
                isScanning = scanState.isScanning,
                lastScanResult = scanState.lastScanResult,
                scanError = scanState.errorMessage,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryUiState(),
        )

    fun onQueryChange(query: String) {
        controls.update { it.copy(query = query) }
    }

    fun onFilterChange(filter: LibraryFilter) {
        controls.update { it.copy(filter = filter) }
    }

    fun onSortChange(sort: LibrarySort) {
        controls.update { it.copy(sort = sort) }
    }

    fun scanLibrary() {
        if (scanState.value.isScanning) return

        viewModelScope.launch {
            scanState.value = LibraryScanState(isScanning = true)
            runCatching { libraryScanner.scan() }
                .onSuccess { result ->
                    scanState.value = LibraryScanState(lastScanResult = result)
                }
                .onFailure { throwable ->
                    scanState.value = LibraryScanState(
                        errorMessage = throwable.message ?: "No se pudo escanear la biblioteca",
                    )
                }
        }
    }

    private fun List<Book>.toVisibleBooks(controls: LibraryControls): List<Book> {
        val normalizedQuery = controls.query.trim().lowercase()
        return asSequence()
            .filter { book -> book.matchesFilter(controls.filter) }
            .filter { book ->
                normalizedQuery.isEmpty() ||
                    book.title.contains(normalizedQuery, ignoreCase = true) ||
                    book.author.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
                    book.fileName.contains(normalizedQuery, ignoreCase = true)
            }
            .sortedWith(controls.sort.comparator)
            .toList()
    }

    private fun Book.matchesFilter(filter: LibraryFilter): Boolean =
        when (filter) {
            LibraryFilter.All -> true
            LibraryFilter.Pdf -> format == BookFormat.Pdf
            LibraryFilter.Epub -> format == BookFormat.Epub
            LibraryFilter.Favorites -> isFavorite
            LibraryFilter.Available -> isAvailable
            LibraryFilter.Unavailable -> !isAvailable
            LibraryFilter.NotStarted -> progressPercent <= 0f
            LibraryFilter.InProgress -> progressPercent > 0f && progressPercent < 100f
            LibraryFilter.Finished -> progressPercent >= 100f
        }
}

data class LibraryUiState(
    val books: List<Book> = emptyList(),
    val totalBooks: Int = 0,
    val pdfBooks: Int = 0,
    val epubBooks: Int = 0,
    val query: String = "",
    val filter: LibraryFilter = LibraryFilter.All,
    val sort: LibrarySort = LibrarySort.Title,
    val isScanning: Boolean = false,
    val lastScanResult: LibraryScanResult? = null,
    val scanError: String? = null,
)

enum class LibraryFilter(val label: String) {
    All("Todos"),
    Pdf("PDF"),
    Epub("EPUB"),
    Favorites("Favoritos"),
    Available("Disponibles"),
    Unavailable("No disponibles"),
    NotStarted("No iniciados"),
    InProgress("En progreso"),
    Finished("Leidos"),
}

enum class LibrarySort(
    val label: String,
    val comparator: Comparator<Book>,
) {
    Title("Nombre", compareBy(String.CASE_INSENSITIVE_ORDER) { it.title }),
    Author(
        "Autor",
        compareBy<Book, String?>(nullsLast(String.CASE_INSENSITIVE_ORDER)) { it.author }
            .thenBy(String.CASE_INSENSITIVE_ORDER) { it.title },
    ),
    DateAdded("Fecha agregada", compareByDescending { it.dateAddedEpochMillis }),
    LastRead("Ultima lectura", compareByDescending<Book> { it.lastReadEpochMillis ?: Long.MIN_VALUE }),
    Progress("Progreso", compareByDescending { it.progressPercent }),
}

private data class LibraryControls(
    val query: String = "",
    val filter: LibraryFilter = LibraryFilter.All,
    val sort: LibrarySort = LibrarySort.Title,
)

private data class LibraryScanState(
    val isScanning: Boolean = false,
    val lastScanResult: LibraryScanResult? = null,
    val errorMessage: String? = null,
)
