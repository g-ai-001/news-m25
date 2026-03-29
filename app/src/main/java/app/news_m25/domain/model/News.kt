package app.news_m25.domain.model

data class News(
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
    val isFavorite: Boolean = false,
    val viewCount: Int = 0
)

enum class NewsCategory(val displayName: String) {
    RECOMMEND("推荐"),
    SOCIAL("社会"),
    ENTERTAINMENT("娱乐"),
    SPORTS("体育"),
    TECH("科技"),
    INTERNATIONAL("国际"),
    MILITARY("军事"),
    FINANCE("财经"),
    HEALTH("健康"),
    TRAVEL("旅游");

    companion object {
        fun fromDisplayName(name: String): NewsCategory? {
            return entries.find { it.displayName == name }
        }
    }
}
