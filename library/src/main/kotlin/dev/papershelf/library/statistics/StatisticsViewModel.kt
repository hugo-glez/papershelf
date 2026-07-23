package dev.papershelf.library.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.papershelf.domain.repository.BookRepository
import dev.papershelf.domain.statistics.LibraryStatistics
import dev.papershelf.domain.statistics.LibraryStatisticsCalculator
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    bookRepository: BookRepository,
) : ViewModel() {
    val uiState: StateFlow<StatisticsUiState> =
        bookRepository.observeBooks()
            .map { books -> LibraryStatisticsCalculator.fromBooks(books).toUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = StatisticsUiState(),
            )

    private fun LibraryStatistics.toUiState(): StatisticsUiState =
        StatisticsUiState(
            totalBooks = totalBooks,
            pdfBooks = pdfBooks,
            epubBooks = epubBooks,
            availableBooks = availableBooks,
            unavailableBooks = unavailableBooks,
            startedBooks = startedBooks,
            finishedBooks = finishedBooks,
            notStartedBooks = notStartedBooks,
            averageProgress = averageProgress,
            favorites = favorites,
            pagesKnown = pagesKnown,
        )
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
