package dev.papershelf.reader.epub

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import dev.papershelf.reader.pdf.PaperShelfPdfActivity
import java.io.File

class EpubReaderLauncher(
    private val context: Context,
) {
    fun open(
        bookId: Long,
        path: String,
    ) {
        val file = File(path)
        if (!file.exists()) {
            Toast.makeText(context, "El archivo no esta disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.files",
            file,
        )

        val intent = Intent(Intent.ACTION_VIEW)
            .setClass(context, PaperShelfPdfActivity::class.java)
            .setDataAndType(uri, "application/epub+zip")
            .putExtra(PaperShelfPdfActivity.EXTRA_BOOK_ID, bookId)
            .putExtra(PaperShelfPdfActivity.EXTRA_PAGE_COUNT, 0)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No se pudo abrir el EPUB", Toast.LENGTH_SHORT).show()
        }
    }
}
