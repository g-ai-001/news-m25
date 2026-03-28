package app.news_m25.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "read_history")
data class ReadHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val newsId: Long,
    val readAt: Long = System.currentTimeMillis()
)