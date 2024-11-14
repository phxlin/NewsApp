package lin.yufan.newsapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lin.yufan.newsapp.data.model.Article
import lin.yufan.newsapp.data.model.NewsResponse
import lin.yufan.newsapp.domain.repository.NewsRepository
import lin.yufan.newsapp.util.Constants.Companion.US
import lin.yufan.newsapp.util.Resource
import retrofit2.Response

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNews: LiveData<Resource<NewsResponse>>
        get() = _breakingNews

    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    private val _searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: LiveData<Resource<NewsResponse>>
        get() = _searchNews

    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews(US)
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        _breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        _breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        _searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        _searchNews.postValue(handleSearchNewsResponse(response))
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.insert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful)
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null)
                    breakingNewsResponse = resultResponse
                else
                    breakingNewsResponse?.articles?.toMutableList()?.addAll(resultResponse.articles)

                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }

        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful)
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null)
                    searchNewsResponse = resultResponse
                else
                    searchNewsResponse?.articles?.toMutableList()?.addAll(resultResponse.articles)

                return Resource.Success(searchNewsResponse ?: resultResponse)
            }

        return Resource.Error(response.message())
    }
}