package dev.papershelf.domain.statistics

import dev.papershelf.domain.model.Book
import dev.papershelf.domain.model.BookFormat
import org.junit.Assert.assertEquals
import org.junit.Test

class LibraryStatisticsCalculatorTest {
    @Test
    fun `fromBooks summarizes library inventory and progress`() {
        val books = listOf(
            book(id = 1, format = BookFormat.Pdf, progress = 0f, isFavorite = true, pageCount = 100),
            book(id = 2, format = BookFormat.Epub, progress = 50f, isAvailable = false, pageCount = null),
            book(id = 3, format = BookFormat.Pdf, progress = 100f, pageCount = 200),
        )

        val stats = LibraryStatisticsCalculator.fromBooks(books)

        assertEquals(3, stats.totalBooks)
        assertEquals(2, stats.pdfBooks)
        assertEquals(1, stats.epubBooks)
        assertEquals(2, stats.availableBooks)
        assertEquals(1, stats.unavailableBooks)
        assertEquals(2, stats.startedBooks)
        assertEquals(1, stats.finishedBooks)
        assertEquals(1, stats.notStartedBooks)
        assertEquals(50f, stats.averageProgress, 0.001f)
        assertEquals(1, stats.favorites)
        assertEquals(300, stats.pagesKnown)
    }

    @Test
    fun `fromBooks returns empty statistics for empty list`() {
        val stats = LibraryStatisticsCalculator.fromBooks(emptyList())

        assertEquals(LibraryStatistics(), stats)
    }

    private fun book(
        id: Long,
        format: BookFormat,
        progress: Float,
        isAvailable: Boolean = true,
        isFavorite: Boolean = false,
        pageCount: Int? = null,
    ): Book =
        Book(
            id = id,
            title = "Book $id",
            author = null,
            format = format,
            path = "/books/book$id",
            fileName = "book$id",
            fileSizeBytes = 1024L,
            pageCount = pageCount,
            thumbnailPath = null,
            dateAddedEpochMillis = id,
            lastReadEpochMillis = null,
            progressPercent = progress,
            isAvailable = isAvailable,
            isFavorite = isFavorite,
        )
}
