package dev.papershelf.reader.epub

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.Asset
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.mediatype.MediaType
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser

@OptIn(ExperimentalReadiumApi::class)
class EpubReaderActivity : FragmentActivity() {
    private val containerId = View.generateViewId()
    private var publication: Publication? = null
    private var asset: Asset? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = FrameLayout(this).apply {
            id = containerId
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        setContentView(container)

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
        val factory = EpubNavigatorFactory(publication)
            .createFragmentFactory(
                null,
                emptyList(),
                EpubPreferences(scroll = false, publisherStyles = true),
                object : EpubNavigatorFragment.Listener {
                    override fun onExternalLinkActivated(url: AbsoluteUrl) = Unit
                },
                null,
                EpubNavigatorFragment.Configuration(),
            )

        supportFragmentManager.fragmentFactory = factory
        supportFragmentManager.commit {
            replace(containerId, EpubNavigatorFragment::class.java, null)
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
        val root = findViewById<FrameLayout>(containerId)
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

    companion object {
        const val EXTRA_PATH = "dev.papershelf.reader.epub.EXTRA_PATH"
    }
}
