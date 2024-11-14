package lin.yufan.newsapp.data.repository

import lin.yufan.newsapp.data.local.ArticleDatabase
import lin.yufan.newsapp.data.model.Article
import lin.yufan.newsapp.data.remote.NewsApi
import lin.yufan.newsapp.domain.repository.NewsRepository

class NewsRepositoryImp(
    private val api: NewsApi,
    private val db: ArticleDatabase
) : NewsRepository {

    override suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        api.getBreakingNews(countryCode, pageNumber)

    override suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        api.searchForNews(searchQuery, pageNumber)

    override suspend fun insert(article: Article) = db.getArticleDao().insert(article)

    override suspend fun delete(article: Article) = db.getArticleDao().delete(article)

    override fun getSavedNews() = db.getArticleDao().getAllArticles()
}