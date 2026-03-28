package app.news_m25

import android.app.Application
import app.news_m25.domain.repository.NewsRepository
import app.news_m25.util.Logger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class NewsApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var newsRepository: NewsRepository

    override fun onCreate() {
        super.onCreate()
        Logger.init(this)
        Logger.d("NewsApplication", "NewsApplication initialized")
        cleanupExpiredCache()
    }

    private fun cleanupExpiredCache() {
        applicationScope.launch {
            try {
                val deletedCount = newsRepository.deleteExpiredNews()
                if (deletedCount > 0) {
                    Logger.d("NewsApplication", "Cleaned up $deletedCount expired news items")
                }
            } catch (e: Exception) {
                Logger.e("NewsApplication", "Failed to cleanup expired cache", e)
            }
        }
    }
}
