package lin.yufan.newsapp.domain.repository

import androidx.lifecycle.LiveData
import lin.yufan.newsapp.data.model.Article
import lin.yufan.newsapp.data.model.NewsResponse
import retrofit2.Response

interface NewsRepository {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse>

    suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponse>

    suspend fun insert(article: Article)

    suspend fun delete(article: Article)

    fun getSavedNews(): LiveData<List<Article>>
}