package app.news_m25.domain.repository

import app.news_m25.domain.model.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getAllNews(): Flow<List<News>>
    fun getNewsByCategory(category: String): Flow<List<News>>
    fun searchNews(keyword: String): Flow<List<News>>
    fun getFavoriteNews(): Flow<List<News>>
    fun getAllCategories(): Flow<List<String>>
    suspend fun getNewsById(id: Long): News?
    suspend fun insertNews(news: News): Long
    suspend fun updateNews(news: News)
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
    suspend fun incrementViewCount(id: Long)
    suspend fun deleteNews(id: Long)
    suspend fun getNewsCount(): Int
    suspend fun getExpiredNewsCount(): Int
    suspend fun deleteExpiredNews(): Int
    suspend fun deleteAllNonFavoriteNews(): Int
}
