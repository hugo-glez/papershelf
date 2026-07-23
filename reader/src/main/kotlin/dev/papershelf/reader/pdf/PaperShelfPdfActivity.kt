package dev.papershelf.reader.pdf

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.artifex.mupdf.viewer.DocumentActivity
import com.artifex.mupdf.viewer.MuPDFCore
import com.artifex.mupdf.viewer.ReaderView
import dagger.hilt.android.EntryPointAccessors
import dev.papershelf.domain.progress.ReadingProgressCalculator
import dev.papershelf.domain.repository.BookmarkRepository
import dev.papershelf.domain.repository.ReadingProgressRepository
import dev.papershelf.reader.di.ReaderEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PaperShelfPdfActivity : DocumentActivity() {
    private lateinit var readingProgressRepository: ReadingProgressRepository
    private lateinit var bookmarkRepository: BookmarkRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val progressScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val bookId: Long
        get() = intent.getLongExtra(EXTRA_BOOK_ID, 0L)
    private val pageCount: Int
        get() = intent.getIntExtra(EXTRA_PAGE_COUNT, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            ReaderEntryPoint::class.java,
        )
        readingProgressRepository = entryPoint.readingProgressRepository()
        bookmarkRepository = entryPoint.bookmarkRepository()
        super.onCreate(savedInstanceState)
        addBookmarkButton()
    }

    override fun onPause() {
        saveCurrentProgress()
        super.onPause()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun addBookmarkButton() {
        val content = window.decorView as? ViewGroup ?: return
        val button = TextView(this).apply {
            text = "Marcador"
            textSize = 14f
            setPadding(18, 10, 18, 10)
            setBackgroundColor(0xCC202124.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener { addBookmark() }
        }

        content.addView(
            button,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.END,
            ).apply {
                topMargin = 24
                rightMargin = 24
            },
        )
    }

    private fun addBookmark() {
        val currentPage = currentPage()
        if (bookId <= 0 || currentPage == null) return

        scope.launch {
            bookmarkRepository.addBookmark(
                bookId = bookId,
                page = currentPage,
                chapter = null,
            )
            Toast.makeText(this@PaperShelfPdfActivity, "Marcador guardado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCurrentProgress() {
        val currentPage = currentPage()
        if (bookId <= 0 || currentPage == null) return

        val knownPageCount = currentPageCount()
        val percent = ReadingProgressCalculator.fromPage(currentPage, knownPageCount)

        progressScope.launch {
            readingProgressRepository.saveProgress(
                bookId = bookId,
                lastPage = currentPage,
                chapter = null,
                percentRead = percent,
                pageCount = knownPageCount,
            )
        }
    }

    private fun currentPage(): Int? {
        val field = runCatching {
            DocumentActivity::class.java.getDeclaredField("mDocView").apply {
                isAccessible = true
            }
        }.getOrNull() ?: return null

        val readerView = field.get(this) as? ReaderView ?: return null
        return readerView.getDisplayedViewIndex()
    }

    private fun currentPageCount(): Int {
        pageCount.takeIf { it > 0 }?.let { return it }

        val coreField = runCatching {
            DocumentActivity::class.java.getDeclaredField("core").apply {
                isAccessible = true
            }
        }.getOrNull() ?: return 0

        val core = coreField.get(this) as? MuPDFCore ?: return 0
        return runCatching { core.countPages() }.getOrDefault(0)
    }

    companion object {
        const val EXTRA_BOOK_ID = "dev.papershelf.reader.pdf.EXTRA_BOOK_ID"
        const val EXTRA_PAGE_COUNT = "dev.papershelf.reader.pdf.EXTRA_PAGE_COUNT"
    }
}
