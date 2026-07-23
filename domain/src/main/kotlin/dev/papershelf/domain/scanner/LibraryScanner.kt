package dev.papershelf.domain.scanner

interface LibraryScanner {
    suspend fun scan(folderPath: String = DEFAULT_BOOKS_FOLDER): LibraryScanResult

    companion object {
        const val DEFAULT_BOOKS_FOLDER = "/storage/emulated/0/Books"
    }
}
