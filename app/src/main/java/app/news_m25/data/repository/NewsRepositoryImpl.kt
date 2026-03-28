package app.news_m25.data.repository

import app.news_m25.data.local.dao.NewsDao
import app.news_m25.data.local.entity.toDomain
import app.news_m25.data.local.entity.toEntity
import app.news_m25.domain.model.News
import app.news_m25.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao
) : NewsRepository {

    override fun getAllNews(): Flow<List<News>> {
        return newsDao.getAllNews().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNewsByCategory(category: String): Flow<List<News>> {
        return newsDao.getNewsByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchNews(keyword: String): Flow<List<News>> {
        return newsDao.searchNews(keyword).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoriteNews(): Flow<List<News>> {
        return newsDao.getFavoriteNews().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllCategories(): Flow<List<String>> {
        return newsDao.getAllCategories()
    }

    override suspend fun getNewsById(id: Long): News? {
        return newsDao.getNewsById(id)?.toDomain()
    }

    override suspend fun insertNews(news: News): Long {
        return newsDao.insertNews(news.toEntity())
    }

    override suspend fun updateNews(news: News) {
        newsDao.updateNews(news.toEntity())
    }

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        newsDao.updateFavorite(id, isFavorite)
    }

    override suspend fun incrementViewCount(id: Long) {
        newsDao.incrementViewCount(id)
    }

    override suspend fun deleteNews(id: Long) {
        newsDao.deleteNews(id)
    }

    override suspend fun getNewsCount(): Int {
        return newsDao.getNewsCount()
    }

    override suspend fun getExpiredNewsCount(): Int {
        return newsDao.getExpiredNewsCount(System.currentTimeMillis())
    }

    override suspend fun deleteExpiredNews(): Int {
        return newsDao.deleteExpiredNews(System.currentTimeMillis())
    }

    override suspend fun deleteAllNonFavoriteNews(): Int {
        return newsDao.deleteAllNonFavoriteNews()
    }
}
