package dev.papershelf.app

import androidx.compose.runtime.Composable
import dev.papershelf.domain.model.BookFormat
import dev.papershelf.library.LibraryRoute
import dev.papershelf.reader.epub.rememberEpubReaderLauncher
import dev.papershelf.reader.pdf.rememberPdfReaderLauncher

@Composable
fun PaperShelfRoot() {
    val pdfReaderLauncher = rememberPdfReaderLauncher()
    val epubReaderLauncher = rememberEpubReaderLauncher()

    LibraryRoute(
        onOpenBook = { book ->
            when (book.format) {
                BookFormat.Pdf -> pdfReaderLauncher.open(book.path)
                BookFormat.Epub -> epubReaderLauncher.open(book.path)
            }
        },
    )
}
