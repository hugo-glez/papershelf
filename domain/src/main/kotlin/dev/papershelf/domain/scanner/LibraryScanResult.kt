package dev.papershelf.domain.scanner

data class LibraryScanResult(
    val scannedFolderPath: String,
    val filesFound: Int,
    val booksAdded: Int,
    val booksUpdated: Int,
    val booksMarkedUnavailable: Int,
)
