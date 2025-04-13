package lin.yufan.whatsnew.ui.features.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lin.yufan.whatsnew.R
import lin.yufan.whatsnew.ui.NewsActivity
import lin.yufan.whatsnew.ui.adapter.NewsAdapter
import lin.yufan.whatsnew.ui.viewmodel.NewsViewModel
import lin.yufan.whatsnew.util.Constants.Companion.QUERY_PAGE_SIZE
import lin.yufan.whatsnew.util.Constants.Companion.US
import lin.yufan.whatsnew.util.Resource

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private val tag = BreakingNewsFragment::class.java.simpleName

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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate =
                isNotLoadingAndNotLastPage
                        && isAtLastItem
                        && isNotAtBeginning
                        && isTotalMoreThanVisible
                        && isScrolling

            if (shouldPaginate) {
                viewModel.getBreakingNews(US)
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
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { data ->
                        newsAdapter.diff.submitList(data.articles.toList())
                        val totalPages = data.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage)
                            (activity as NewsActivity).findViewById<RecyclerView>(R.id.rvBreakingNews)
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
        (activity as NewsActivity).findViewById<RecyclerView>(R.id.rvBreakingNews).apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    private fun showProgressBar() {
        (activity as NewsActivity).findViewById<ProgressBar>(R.id.loadingBreakingNewsProgressBar).visibility =
            View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        (activity as NewsActivity).findViewById<ProgressBar>(R.id.loadingBreakingNewsProgressBar).visibility =
            View.INVISIBLE
        isLoading = false
    }
}