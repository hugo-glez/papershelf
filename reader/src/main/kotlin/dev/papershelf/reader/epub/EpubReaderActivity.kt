package dev.papershelf.reader.epub

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.papershelf.domain.repository.BookmarkRepository
import dev.papershelf.domain.repository.ReadingProgressRepository
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.Asset
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.mediatype.MediaType
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser

@OptIn(ExperimentalReadiumApi::class)
@AndroidEntryPoint
class EpubReaderActivity : FragmentActivity() {
    @Inject lateinit var readingProgressRepository: ReadingProgressRepository
    @Inject lateinit var bookmarkRepository: BookmarkRepository

    private val rootId = View.generateViewId()
    private val containerId = View.generateViewId()
    private var publication: Publication? = null
    private var asset: Asset? = null
    private var currentLocator: Locator? = null
    private var currentPage: Int? = null
    private val bookId: Long
        get() = intent.getLongExtra(EXTRA_BOOK_ID, 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = FrameLayout(this).apply {
            id = rootId
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            addView(
                FrameLayout(this@EpubReaderActivity).apply { id = containerId },
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                ),
            )
        }
        setContentView(root)

        val path = intent.getStringExtra(EXTRA_PATH)
        if (path.isNullOrBlank()) {
            showMessage("No se encontro el EPUB")
            return
        }

        showLoading()
        lifecycleScope.launch {
            openEpub(File(path))
        }
    }

    override fun onDestroy() {
        saveCurrentProgress()
        publication?.close()
        asset?.close()
        super.onDestroy()
    }

    private suspend fun openEpub(file: File) {
        if (!file.exists()) {
            showMessage("El archivo no esta disponible")
            return
        }

        val result = withContext(Dispatchers.IO) {
            runCatching { loadPublication(file) }
        }

        result
            .onSuccess { publication ->
                this.publication = publication
                showPublication(publication)
            }
            .onFailure { throwable ->
                showMessage(throwable.message ?: "No se pudo abrir el EPUB")
            }
    }

    private suspend fun loadPublication(file: File): Publication {
        val httpClient = DefaultHttpClient()
        val assetRetriever = AssetRetriever(contentResolver, httpClient)
        val asset = when (val retrieved = assetRetriever.retrieve(file, MediaType.EPUB)) {
            is Try.Success -> retrieved.value
            is Try.Failure -> error("No se pudo leer el archivo EPUB")
        }
        this.asset = asset

        val parser = DefaultPublicationParser(
            context = this,
            httpClient = httpClient,
            assetRetriever = assetRetriever,
            pdfFactory = null,
        )
        val opener = PublicationOpener(parser)

        return when (val opened = opener.open(asset, file.nameWithoutExtension, allowUserInteraction = false)) {
            is Try.Success -> opened.value
            is Try.Failure -> error("No se pudo abrir la publicacion EPUB")
        }
    }

    private fun showPublication(publication: Publication) {
        ensureNavigatorContainer()
        val factory = EpubNavigatorFactory(publication)
            .createFragmentFactory(
                null,
                emptyList(),
                EpubPreferences(scroll = false, publisherStyles = true),
                object : EpubNavigatorFragment.Listener {
                    override fun onExternalLinkActivated(url: AbsoluteUrl) = Unit
                },
                object : EpubNavigatorFragment.PaginationListener {
                    override fun onPageChanged(pageIndex: Int, totalPages: Int, locator: Locator) {
                        currentPage = pageIndex
                        currentLocator = locator
                        saveCurrentProgress(totalPages)
                    }
                },
                EpubNavigatorFragment.Configuration(),
            )

        supportFragmentManager.fragmentFactory = factory
        supportFragmentManager.commit {
            replace(containerId, EpubNavigatorFragment::class.java, null)
        }
        addBookmarkButton()
    }

    private fun addBookmarkButton() {
        val root = findViewById<FrameLayout>(rootId)
        val button = Button(this).apply {
            text = "Marcador"
            textSize = 13f
            setOnClickListener { addBookmark() }
        }
        root.addView(
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
        if (bookId <= 0) return
        lifecycleScope.launch {
            bookmarkRepository.addBookmark(
                bookId = bookId,
                page = currentPage,
                chapter = currentLocator?.title,
            )
            android.widget.Toast.makeText(
                this@EpubReaderActivity,
                "Marcador guardado",
                android.widget.Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun saveCurrentProgress(totalPages: Int? = null) {
        if (bookId <= 0) return
        val locator = currentLocator
        val page = currentPage
        val percent = locator?.locations?.totalProgression?.let { (it * 100).toFloat() }
            ?: if (page != null && totalPages != null && totalPages > 0) {
                ((page + 1).toFloat() / totalPages.toFloat()) * 100f
            } else {
                0f
            }

        lifecycleScope.launch {
            readingProgressRepository.saveProgress(
                bookId = bookId,
                lastPage = page,
                chapter = locator?.title,
                percentRead = percent,
            )
        }
    }

    private fun showLoading() {
        val progress = ProgressBar(this).apply {
            isIndeterminate = true
        }
        setCenteredContent(progress)
    }

    private fun showMessage(message: String) {
        val textView = TextView(this).apply {
            text = message
            gravity = Gravity.CENTER
            textSize = 16f
        }
        setCenteredContent(textView)
    }

    private fun setCenteredContent(view: android.view.View) {
        val root = findViewById<FrameLayout>(rootId)
        root.removeAllViews()
        root.addView(
            view,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER,
            ),
        )
    }

    private fun ensureNavigatorContainer() {
        val root = findViewById<FrameLayout>(rootId)
        root.removeAllViews()
        root.addView(
            FrameLayout(this).apply { id = containerId },
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )
    }

    companion object {
        const val EXTRA_BOOK_ID = "dev.papershelf.reader.epub.EXTRA_BOOK_ID"
        const val EXTRA_PATH = "dev.papershelf.reader.epub.EXTRA_PATH"
    }
}
