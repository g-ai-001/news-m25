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
    val videoUrl: String? = null,
    val imageUrls: String = "",
    val isFavorite: Boolean = false,
    val viewCount: Int = 0,
    val isReadLater: Boolean = false,
    val rating: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val cachedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + CACHE_EXPIRY_DURATION
) {
    companion object {
        const val CACHE_EXPIRY_DURATION = 7 * 24 * 60 * 60 * 1000L // 7 days
    }

    fun isExpired(): Boolean = System.currentTimeMillis() > expiresAt

    fun getImageUrlsList(): List<String> {
        return if (imageUrls.isBlank()) emptyList()
        else imageUrls.split("|").filter { it.isNotBlank() }
    }
}
