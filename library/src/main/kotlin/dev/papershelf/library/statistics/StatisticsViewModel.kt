package dev.papershelf.library.statistics

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.papershelf.domain.model.Book
import dev.papershelf.domain.model.BookFormat
import dev.papershelf.domain.repository.BookRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    bookRepository: BookRepository,
) : ViewModel() {
    val uiState: StateFlow<StatisticsUiState> =
        bookRepository.observeBooks()
            .map { books -> books.toStatistics() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = StatisticsUiState(),
            )

    private fun List<Book>.toStatistics(): StatisticsUiState {
        val started = count { it.progressPercent > 0f }
        val finished = count { it.progressPercent >= 100f }
        return StatisticsUiState(
            totalBooks = size,
            pdfBooks = count { it.format == BookFormat.Pdf },
            epubBooks = count { it.format == BookFormat.Epub },
            availableBooks = count { it.isAvailable },
            unavailableBooks = count { !it.isAvailable },
            startedBooks = started,
            finishedBooks = finished,
            notStartedBooks = size - started,
            averageProgress = if (isEmpty()) 0f else sumOf { it.progressPercent.toDouble() }.toFloat() / size,
            favorites = count { it.isFavorite },
            pagesKnown = mapNotNull { it.pageCount }.sum(),
        )
    }
}

data class StatisticsUiState(
    val totalBooks: Int = 0,
    val pdfBooks: Int = 0,
    val epubBooks: Int = 0,
    val availableBooks: Int = 0,
    val unavailableBooks: Int = 0,
    val startedBooks: Int = 0,
    val finishedBooks: Int = 0,
    val notStartedBooks: Int = 0,
    val averageProgress: Float = 0f,
    val favorites: Int = 0,
    val pagesKnown: Int = 0,
)
