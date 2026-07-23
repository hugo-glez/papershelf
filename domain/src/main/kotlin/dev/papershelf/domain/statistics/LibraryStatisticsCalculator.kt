package dev.papershelf.domain.statistics

import dev.papershelf.domain.model.Book
import dev.papershelf.domain.model.BookFormat
import dev.papershelf.domain.progress.ReadingProgressCalculator

object LibraryStatisticsCalculator {
    fun fromBooks(books: List<Book>): LibraryStatistics {
        val started = books.count { it.progressPercent > 0f }
        val finished = books.count { it.progressPercent >= 100f }
        return LibraryStatistics(
            totalBooks = books.size,
            pdfBooks = books.count { it.format == BookFormat.Pdf },
            epubBooks = books.count { it.format == BookFormat.Epub },
            availableBooks = books.count { it.isAvailable },
            unavailableBooks = books.count { !it.isAvailable },
            startedBooks = started,
            finishedBooks = finished,
            notStartedBooks = books.size - started,
            averageProgress = books.averageProgress(),
            favorites = books.count { it.isFavorite },
            pagesKnown = books.mapNotNull { it.pageCount }.sum(),
        )
    }

    private fun List<Book>.averageProgress(): Float {
        if (isEmpty()) return 0f
        return sumOf { ReadingProgressCalculator.bound(it.progressPercent).toDouble() }.toFloat() / size
    }
}
