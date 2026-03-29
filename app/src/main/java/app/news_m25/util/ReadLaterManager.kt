package app.news_m25.util

import app.news_m25.domain.repository.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadLaterManager @Inject constructor(
    private val newsRepository: NewsRepository
) {
    fun toggle(
        scope: CoroutineScope,
        newsId: Long,
        currentReadLaterState: Boolean,
        onError: ((Exception) -> Unit)? = null
    ) {
        scope.launch {
            try {
                newsRepository.toggleReadLater(newsId, !currentReadLaterState)
                Logger.d("ReadLaterManager", "Toggled read later for news $newsId")
            } catch (e: Exception) {
                Logger.e("ReadLaterManager", "Failed to toggle read later", e)
                onError?.invoke(e)
            }
        }
    }
}