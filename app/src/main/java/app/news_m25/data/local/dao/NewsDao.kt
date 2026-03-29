package app.news_m25.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.news_m25.data.local.entity.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news ORDER BY publishedAt DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE category = :category ORDER BY publishedAt DESC")
    fun getNewsByCategory(category: String): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getNewsById(id: Long): NewsEntity?

    @Query("SELECT * FROM news WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%' ORDER BY publishedAt DESC")
    fun searchNews(keyword: String): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE isFavorite = 1 ORDER BY publishedAt DESC")
    fun getFavoriteNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE isReadLater = 1 ORDER BY publishedAt DESC")
    fun getReadLaterNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE rating > 0 ORDER BY rating DESC, publishedAt DESC")
    fun getRatedNews(): Flow<List<NewsEntity>>

    @Query("SELECT COUNT(*) FROM news")
    suspend fun getNewsCount(): Int

    @Query("SELECT COUNT(*) FROM news WHERE :timestamp > expiresAt")
    suspend fun getExpiredNewsCount(timestamp: Long): Int

    @Query("DELETE FROM news WHERE :timestamp > expiresAt AND isFavorite = 0")
    suspend fun deleteExpiredNews(timestamp: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNews(newsList: List<NewsEntity>)

    @Update
    suspend fun updateNews(news: NewsEntity)

    @Query("UPDATE news SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE news SET isReadLater = :isReadLater WHERE id = :id")
    suspend fun updateReadLater(id: Long, isReadLater: Boolean)

    @Query("UPDATE news SET rating = :rating WHERE id = :id")
    suspend fun updateRating(id: Long, rating: Int)

    @Query("UPDATE news SET viewCount = viewCount + 1 WHERE id = :id")
    suspend fun incrementViewCount(id: Long)

    @Query("SELECT DISTINCT category FROM news")
    fun getAllCategories(): Flow<List<String>>

    @Query("DELETE FROM news WHERE id = :id")
    suspend fun deleteNews(id: Long)

    @Query("DELETE FROM news WHERE isFavorite = 0")
    suspend fun deleteAllNonFavoriteNews(): Int
}
