package lin.yufan.newsapp.ui.features.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import lin.yufan.newsapp.R
import lin.yufan.newsapp.ui.NewsActivity
import lin.yufan.newsapp.ui.viewmodel.NewsViewModel

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        val article = args.article

        (activity as NewsActivity).findViewById<WebView>(R.id.webView).apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }
        (activity as NewsActivity).findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

    inner class WebViewClient : android.webkit.WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            (activity as NewsActivity).findViewById<ProgressBar>(R.id.loadingNewsProgressBar).visibility =
                View.GONE
        }
    }
}