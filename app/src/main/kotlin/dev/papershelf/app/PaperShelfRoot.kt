package dev.papershelf.app

import androidx.compose.runtime.Composable
import dev.papershelf.domain.model.BookFormat
import dev.papershelf.library.LibraryRoute
import dev.papershelf.reader.pdf.rememberPdfReaderLauncher

@Composable
fun PaperShelfRoot() {
    val pdfReaderLauncher = rememberPdfReaderLauncher()

    LibraryRoute(
        onOpenBook = { book ->
            if (book.format == BookFormat.Pdf) {
                pdfReaderLauncher.open(book.path)
            }
        },
    )
}
