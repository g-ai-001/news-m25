package app.news_m25

import android.app.Application
import app.news_m25.util.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.init(this)
        Logger.i("NewsApplication", "NewsApplication initialized")
    }
}
