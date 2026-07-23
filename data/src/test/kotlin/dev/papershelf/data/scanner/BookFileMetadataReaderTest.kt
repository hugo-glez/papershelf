package dev.papershelf.data.scanner

import dev.papershelf.database.entity.BookFormatEntity
import java.io.File
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
        assertEquals(file.nameWithoutExtension, metadata.title)
        assertEquals(file.absolutePath, metadata.path)
        assertEquals(file.name, metadata.fileName)
        assertTrue(metadata.fileSizeBytes > 0)
    }

    @Test
    fun `read extracts epub metadata`() {
        val file = File.createTempFile("PaperShelf Sample", ".epub").apply {
            writeText("content")
        }

        val metadata = reader.read(file)

        requireNotNull(metadata)
        assertEquals(BookFormatEntity.Epub, metadata.format)
    }

    @Test
    fun `read ignores unsupported files`() {
        val file = File.createTempFile("PaperShelf Sample", ".txt")

        assertNull(reader.read(file))
    }
}
