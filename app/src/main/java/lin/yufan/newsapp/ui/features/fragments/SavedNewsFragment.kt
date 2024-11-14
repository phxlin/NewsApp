package lin.yufan.newsapp.ui.features.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import lin.yufan.newsapp.R
import lin.yufan.newsapp.ui.NewsActivity
import lin.yufan.newsapp.ui.adapter.NewsAdapter
import lin.yufan.newsapp.ui.viewmodel.NewsViewModel

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        setRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.diff.currentList[position]

                viewModel.deleteArticle(article)
                Snackbar.make(view, "Deleted successfully", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView((activity as NewsActivity).findViewById<RecyclerView>(R.id.rvSavedNews))
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner) { article ->
            newsAdapter.diff.submitList(article)
        }
    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter()
        (activity as NewsActivity).findViewById<RecyclerView>(R.id.rvSavedNews).apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}