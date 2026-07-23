package dev.papershelf.domain.statistics

data class LibraryStatistics(
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
