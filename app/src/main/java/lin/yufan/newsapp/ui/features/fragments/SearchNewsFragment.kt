package lin.yufan.newsapp.ui.features.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lin.yufan.newsapp.R
import lin.yufan.newsapp.ui.NewsActivity
import lin.yufan.newsapp.ui.adapter.NewsAdapter
import lin.yufan.newsapp.ui.viewmodel.NewsViewModel
import lin.yufan.newsapp.util.Constants
import lin.yufan.newsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import lin.yufan.newsapp.util.Resource

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private val tag = SearchNewsFragment::class.java.simpleName

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginate =
                isNotLoadingAndNotLastPage
                        && isAtLastItem
                        && isNotAtBeginning
                        && isTotalMoreThanVisible
                        && isScrolling

            if (shouldPaginate) {
                viewModel.searchNews(
                    (activity as NewsActivity).findViewById<EditText>(R.id.etSearch).toString()
                )
                isScrolling = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        setRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job? = null
        (activity as NewsActivity).findViewById<EditText>(R.id.etSearch)
            .addTextChangedListener { editable ->
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_NEWS_TIME_DELAY)
                    editable?.let {
                        if (editable.toString().isNotEmpty()) {
                            newsAdapter.diff.submitList(emptyList())
                            viewModel.searchNewsPage = 1
                            viewModel.searchNewsResponse = null
                            viewModel.searchNews(editable.toString())
                        }
                    }
                }
            }

        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { data ->
                        newsAdapter.diff.submitList(data.articles.toList())
                        val totalPages = data.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage)
                            (activity as NewsActivity).findViewById<RecyclerView>(R.id.rvSearchNews)
                                .setPadding(0, 0, 0, 0)
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(tag, "An error occurred: $message")
                    }
                }

                is Resource.Loading -> showProgressBar()
            }
        }
    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter()
        (activity as NewsActivity).findViewById<RecyclerView>(R.id.rvSearchNews).apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    private fun showProgressBar() {
        (activity as NewsActivity).findViewById<ProgressBar>(R.id.loadingSearchNewsProgressBar).visibility =
            View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        (activity as NewsActivity).findViewById<ProgressBar>(R.id.loadingSearchNewsProgressBar).visibility =
            View.INVISIBLE
        isLoading = false
    }
}