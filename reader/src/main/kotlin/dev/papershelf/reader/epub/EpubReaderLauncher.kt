package dev.papershelf.reader.epub

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.io.File

class EpubReaderLauncher(
    private val context: Context,
) {
    fun open(path: String) {
        val file = File(path)
        if (!file.exists()) {
            Toast.makeText(context, "El archivo no esta disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(context, EpubReaderActivity::class.java)
            .putExtra(EpubReaderActivity.EXTRA_PATH, path)

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No se pudo abrir el EPUB", Toast.LENGTH_SHORT).show()
        }
    }
}
