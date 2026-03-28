package app.news_m25.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.news_m25.data.local.entity.ReadHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadHistoryDao {
    @Query("""
        SELECT n.* FROM news n
        INNER JOIN read_history rh ON n.id = rh.newsId
        ORDER BY rh.readAt DESC
    """)
    fun getReadHistory(): Flow<List<app.news_m25.data.local.entity.NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadHistory(readHistory: ReadHistoryEntity)

    @Query("DELETE FROM read_history")
    suspend fun clearHistory()

    @Query("DELETE FROM read_history WHERE newsId = :newsId")
    suspend fun deleteHistoryItem(newsId: Long)
}