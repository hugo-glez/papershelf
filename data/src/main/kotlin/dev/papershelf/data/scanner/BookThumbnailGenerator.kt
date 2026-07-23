package dev.papershelf.data.scanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.papershelf.database.entity.BookFormatEntity
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookThumbnailGenerator @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ThumbnailGenerator {
    override fun thumbnailFor(
        file: File,
        format: BookFormatEntity,
    ): String? {
        val output = File(context.cacheDir, "book-covers/${file.stableName()}.jpg")
        if (output.exists() && output.lastModified() >= file.lastModified()) return output.absolutePath
        output.parentFile?.mkdirs()

        val bitmap = when (format) {
            BookFormatEntity.Pdf -> renderPdfCover(file)
            BookFormatEntity.Epub -> renderEpubCover(file)
        } ?: return null

        return runCatching {
            output.outputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 86, stream)
            }
            output.setLastModified(file.lastModified())
            bitmap.recycle()
            output.absolutePath
        }.getOrNull()
    }

    private fun renderPdfCover(file: File): Bitmap? =
        runCatching {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
                PdfRenderer(descriptor).use { renderer ->
                    if (renderer.pageCount <= 0) return null
                    renderer.openPage(0).use { page ->
                        val targetWidth = 240
                        val targetHeight = (targetWidth * page.height / page.width.toFloat()).toInt().coerceAtLeast(320)
                        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                        Canvas(bitmap).drawColor(Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmap
                    }
                }
            }
        }.getOrNull()

    private fun renderEpubCover(file: File): Bitmap? =
        runCatching {
            ZipFile(file).use { zip ->
                val opfPath = zip.readText("META-INF/container.xml")
                    ?.let { ROOTFILE_REGEX.find(it)?.groupValues?.getOrNull(1) }
                    ?: return null
                val opf = zip.readText(opfPath) ?: return null
                val opfDir = opfPath.substringBeforeLast('/', missingDelimiterValue = "")
                val manifest = MANIFEST_ITEM_REGEX.findAll(opf)
                    .mapNotNull { match ->
                        val attributes = match.groupValues[1].parseXmlAttributes()
                        val id = attributes["id"] ?: return@mapNotNull null
                        val href = attributes["href"] ?: return@mapNotNull null
                        id to ManifestItem(
                            href = resolvePath(opfDir, href),
                            properties = attributes["properties"].orEmpty(),
                        )
                    }
                    .toMap()

                val coverId = META_COVER_REGEX.find(opf)?.groupValues?.getOrNull(1)
                val coverPath = coverId?.let { manifest[it]?.href }
                    ?: manifest.values.firstOrNull { it.properties.split(' ').contains("cover-image") }?.href
                    ?: return null
                val entry = zip.getEntry(coverPath) ?: return null
                val bytes = zip.getInputStream(entry).use { it.readBytes() }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        }.getOrNull()

    private fun ZipFile.readText(path: String): String? {
        val entry = getEntry(path) ?: return null
        return getInputStream(entry).bufferedReader().use { it.readText() }
    }

    private fun String.parseXmlAttributes(): Map<String, String> =
        ATTRIBUTE_REGEX.findAll(this).associate { it.groupValues[1] to it.groupValues[2] }

    private fun resolvePath(
        baseDirectory: String,
        href: String,
    ): String {
        val parts = (if (baseDirectory.isBlank()) href else "$baseDirectory/$href")
            .substringBefore('#')
            .split('/')
            .filter { it.isNotBlank() && it != "." }
        val normalized = ArrayDeque<String>()
        parts.forEach { part ->
            if (part == "..") {
                if (normalized.isNotEmpty()) normalized.removeLast()
            } else {
                normalized.addLast(part)
            }
        }
        return normalized.joinToString("/")
    }

    private fun File.stableName(): String {
        val input = "$absolutePath:${lastModified()}:${length()}"
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private data class ManifestItem(
        val href: String,
        val properties: String,
    )

    private val ROOTFILE_REGEX = Regex("""full-path\s*=\s*["']([^"']+)["']""", RegexOption.IGNORE_CASE)
    private val MANIFEST_ITEM_REGEX = Regex("""<item\b([^>]*)/?>""", RegexOption.IGNORE_CASE)
    private val ATTRIBUTE_REGEX = Regex("""([\w:.-]+)\s*=\s*["']([^"']*)["']""")
    private val META_COVER_REGEX = Regex(
        """<meta\b[^>]*name\s*=\s*["']cover["'][^>]*content\s*=\s*["']([^"']+)["'][^>]*/?>""",
        RegexOption.IGNORE_CASE,
    )
}
