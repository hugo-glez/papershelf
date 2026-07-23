package dev.papershelf.data.scanner

import dev.papershelf.database.entity.BookFormatEntity
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BookFileMetadataReaderTest {
    private val reader = BookFileMetadataReader(
        object : ThumbnailGenerator {
            override fun thumbnailFor(
                file: File,
                format: BookFormatEntity,
            ): String? = null
        },
    )

    @Test
    fun `read extracts pdf metadata case insensitively`() {
        val file = File.createTempFile("PaperShelf Sample", ".PDF").apply {
            writeText("content")
        }

        val metadata = reader.read(file)

        requireNotNull(metadata)
        assertEquals(BookFormatEntity.Pdf, metadata.format)
        assertTrue(metadata.title.startsWith("PaperShelf Sample"))
        assertEquals(file.absolutePath, metadata.path)
        assertEquals(file.name, metadata.fileName)
        assertTrue(metadata.fileSizeBytes > 0)
    }

    @Test
    fun `read extracts epub format`() {
        val file = File.createTempFile("PaperShelf Sample", ".epub").apply {
            writeText("content")
        }

        val metadata = reader.read(file)

        requireNotNull(metadata)
        assertEquals(BookFormatEntity.Epub, metadata.format)
    }

    @Test
    fun `read uses epub title and author from package metadata`() {
        val file = File.createTempFile("ignored-file-name", ".epub").apply {
            writeMinimalEpub(
                title = "The Actual Book Title",
                author = "Jane Writer",
            )
        }

        val metadata = reader.read(file)

        requireNotNull(metadata)
        assertEquals("The Actual Book Title", metadata.title)
        assertEquals("Jane Writer", metadata.author)
    }

    @Test
    fun `read ignores unsupported files`() {
        val file = File.createTempFile("PaperShelf Sample", ".txt")

        assertNull(reader.read(file))
    }

    private fun File.writeMinimalEpub(
        title: String,
        author: String,
    ) {
        ZipOutputStream(outputStream()).use { zip ->
            zip.putText(
                "META-INF/container.xml",
                """<?xml version="1.0"?>
                <container>
                  <rootfiles>
                    <rootfile full-path="OEBPS/content.opf" />
                  </rootfiles>
                </container>""",
            )
            zip.putText(
                "OEBPS/content.opf",
                """<?xml version="1.0"?>
                <package xmlns:dc="http://purl.org/dc/elements/1.1/">
                  <metadata>
                    <dc:title>$title</dc:title>
                    <dc:creator>$author</dc:creator>
                  </metadata>
                </package>""",
            )
        }
    }

    private fun ZipOutputStream.putText(
        name: String,
        content: String,
    ) {
        putNextEntry(ZipEntry(name))
        write(content.trimIndent().toByteArray())
        closeEntry()
    }
}
