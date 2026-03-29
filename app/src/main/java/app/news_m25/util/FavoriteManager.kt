package app.news_m25.util

import app.news_m25.domain.repository.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteManager @Inject constructor(
    private val newsRepository: NewsRepository
) {
    fun toggle(
        scope: CoroutineScope,
        newsId: Long,
        currentFavoriteState: Boolean,
        onError: ((Exception) -> Unit)? = null
    ) {
        scope.launch {
            try {
                newsRepository.toggleFavorite(newsId, !currentFavoriteState)
                Logger.d("FavoriteManager", "Toggled favorite for news $newsId")
            } catch (e: Exception) {
                Logger.e("FavoriteManager", "Failed to toggle favorite", e)
                onError?.invoke(e)
            }
        }
    }
}
