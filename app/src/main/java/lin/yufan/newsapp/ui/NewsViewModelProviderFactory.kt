package lin.yufan.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import lin.yufan.newsapp.domain.repository.NewsRepository
import lin.yufan.newsapp.ui.viewmodel.NewsViewModel

@Suppress("UNCHECKED_CAST")
class NewsViewModelProviderFactory(
    private val repository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(repository) as T
    }
}