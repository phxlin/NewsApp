package lin.yufan.whatsnew.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import lin.yufan.whatsnew.domain.repository.NewsRepository
import lin.yufan.whatsnew.ui.viewmodel.NewsViewModel

@Suppress("UNCHECKED_CAST")
class NewsViewModelProviderFactory(
    private val repository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(repository) as T
    }
}