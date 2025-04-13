package lin.yufan.whatsnew.data.repository

import lin.yufan.whatsnew.data.local.ArticleDao
import lin.yufan.whatsnew.data.model.Article
import lin.yufan.whatsnew.data.remote.NewsApi
import lin.yufan.whatsnew.domain.repository.NewsRepository

class NewsRepositoryImp(
    private val api: NewsApi,
    private val dao: ArticleDao
) : NewsRepository {

    override suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        api.getBreakingNews(countryCode, pageNumber)

    override suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        api.searchForNews(searchQuery, pageNumber)

    override suspend fun insert(article: Article) = dao.insert(article)

    override suspend fun delete(article: Article) = dao.delete(article)

    override fun getSavedNews() = dao.getAllArticles()
}