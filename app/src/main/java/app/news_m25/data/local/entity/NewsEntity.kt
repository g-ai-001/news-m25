package app.news_m25.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val summary: String,
    val content: String,
    val category: String,
    val source: String,
    val author: String,
    val publishedAt: Long,
    val imageUrl: String?,
    val isFavorite: Boolean = false,
    val viewCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
