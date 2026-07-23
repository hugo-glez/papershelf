package dev.papershelf.data.scanner

import java.io.File
import java.util.zip.ZipFile

class EpubMetadataReader {
    fun read(file: File): EpubMetadata? =
        runCatching {
            ZipFile(file).use { zip ->
                val opfPath = zip.readText("META-INF/container.xml")
                    ?.let { ROOTFILE_REGEX.find(it)?.groupValues?.getOrNull(1) }
                    ?: return null
                val opf = zip.readText(opfPath) ?: return null
                EpubMetadata(
                    title = DC_TITLE_REGEX.find(opf)?.groupValues?.getOrNull(1)?.cleanXmlText(),
                    author = DC_CREATOR_REGEX.find(opf)?.groupValues?.getOrNull(1)?.cleanXmlText(),
                )
            }
        }.getOrNull()

    private fun ZipFile.readText(path: String): String? {
        val entry = getEntry(path) ?: return null
        return getInputStream(entry).bufferedReader().use { it.readText() }
    }

    private fun String.cleanXmlText(): String =
        replace(Regex("<[^>]+>"), "")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .trim()
            .ifBlank { "" }

    private val ROOTFILE_REGEX = Regex("""full-path\s*=\s*["']([^"']+)["']""", RegexOption.IGNORE_CASE)
    private val DC_TITLE_REGEX = Regex("""<dc:title[^>]*>(.*?)</dc:title>""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val DC_CREATOR_REGEX = Regex("""<dc:creator[^>]*>(.*?)</dc:creator>""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
}

data class EpubMetadata(
    val title: String?,
    val author: String?,
)
