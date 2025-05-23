package lin.yufan.whatsnew.ui.features.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lin.yufan.whatsnew.R
import lin.yufan.whatsnew.databinding.FragmentSearchNewsBinding
import lin.yufan.whatsnew.ui.NewsActivity
import lin.yufan.whatsnew.ui.adapter.NewsAdapter
import lin.yufan.whatsnew.ui.viewmodel.NewsViewModel
import lin.yufan.whatsnew.util.Constants
import lin.yufan.whatsnew.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import lin.yufan.whatsnew.util.Resource

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private val tag = SearchNewsFragment::class.java.simpleName

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    private lateinit var binding: FragmentSearchNewsBinding

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
                    binding.etSearch.toString()
                )
                isScrolling = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchNewsBinding.bind(view)

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
        binding.etSearch
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
                            binding.rvSearchNews
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
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    private fun showProgressBar() {
        binding.loadingSearchNewsProgressBar.visibility =
            View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        binding.loadingSearchNewsProgressBar.visibility =
            View.INVISIBLE
        isLoading = false
    }
}