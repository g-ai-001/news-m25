package app.news_m25.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.news_m25.domain.repository.NewsRepository
import app.news_m25.util.Logger
import app.news_m25.util.SettingsManager
import app.news_m25.util.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(application) {

    val isDarkMode: StateFlow<Boolean> = ThemeManager.isDarkModeEnabled(application)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val textSize: StateFlow<Int> = SettingsManager.getTextSize(application)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1
        )

    private val _cacheSize = MutableStateFlow("0 MB")
    val cacheSize: StateFlow<String> = _cacheSize.asStateFlow()

    private val _newsCacheSize = MutableStateFlow("0 MB")
    val newsCacheSize: StateFlow<String> = _newsCacheSize.asStateFlow()

    private val _expiredNewsCount = MutableStateFlow(0)
    val expiredNewsCount: StateFlow<Int> = _expiredNewsCount.asStateFlow()

    init {
        viewModelScope.launch {
            updateCacheSize()
            updateNewsCacheStats()
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            ThemeManager.setDarkMode(getApplication(), enabled)
        }
    }

    fun setTextSize(size: Int) {
        viewModelScope.launch {
            SettingsManager.setTextSize(getApplication(), size)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val cacheDir = getApplication<Application>().cacheDir
                    val externalCacheDir = getApplication<Application>().getExternalCacheDir()

                    var totalSize = 0L
                    cacheDir?.let { dir ->
                        totalSize += calculateDirSize(dir)
                        dir.deleteRecursively()
                    }
                    externalCacheDir?.let { dir ->
                        totalSize += calculateDirSize(dir)
                        dir.deleteRecursively()
                    }

                    Logger.d("SettingsViewModel", "Cleared app cache: ${formatSize(totalSize)}")
                    updateCacheSize()
                } catch (e: Exception) {
                    Logger.e("SettingsViewModel", "Failed to clear cache", e)
                }
            }
        }
    }

    fun clearNewsCache() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val deletedCount = newsRepository.deleteAllNonFavoriteNews()
                    Logger.d("SettingsViewModel", "Cleared $deletedCount non-favorite news items")
                    updateNewsCacheStats()
                } catch (e: Exception) {
                    Logger.e("SettingsViewModel", "Failed to clear news cache", e)
                }
            }
        }
    }

    fun clearExpiredNews() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val deletedCount = newsRepository.deleteExpiredNews()
                    Logger.d("SettingsViewModel", "Cleared $deletedCount expired news items")
                    updateNewsCacheStats()
                } catch (e: Exception) {
                    Logger.e("SettingsViewModel", "Failed to clear expired news", e)
                }
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val context = getApplication<Application>()

                    // Clear cache
                    clearCacheInternal(context)

                    // Clear news cache
                    newsRepository.deleteAllNonFavoriteNews()

                    // Clear all preferences via DataStore
                    SettingsManager.clearAll(context)

                    Logger.d("SettingsViewModel", "All data cleared")
                    updateCacheSize()
                    updateNewsCacheStats()
                } catch (e: Exception) {
                    Logger.e("SettingsViewModel", "Failed to clear all data", e)
                }
            }
        }
    }

    private fun clearCacheInternal(context: Application) {
        val cacheDir = context.cacheDir
        val externalCacheDir = context.getExternalCacheDir()

        cacheDir?.let { dir ->
            calculateDirSize(dir)
            dir.deleteRecursively()
        }
        externalCacheDir?.let { dir ->
            calculateDirSize(dir)
            dir.deleteRecursively()
        }
    }

    private suspend fun updateCacheSize() {
        withContext(Dispatchers.IO) {
            try {
                val cacheDir = getApplication<Application>().cacheDir
                val externalCacheDir = getApplication<Application>().getExternalCacheDir()

                var totalSize = 0L
                cacheDir?.let { totalSize += calculateDirSize(it) }
                externalCacheDir?.let { totalSize += calculateDirSize(it) }

                _cacheSize.value = formatSize(totalSize)
            } catch (e: Exception) {
                _cacheSize.value = "0 MB"
            }
        }
    }

    private suspend fun updateNewsCacheStats() {
        withContext(Dispatchers.IO) {
            try {
                val newsCount = newsRepository.getNewsCount()
                val expiredCount = newsRepository.getExpiredNewsCount()
                _expiredNewsCount.value = expiredCount
                // Estimate news cache size: ~5KB per news item average
                val estimatedSize = newsCount * 5 * 1024L
                _newsCacheSize.value = formatSize(estimatedSize)
            } catch (e: Exception) {
                _newsCacheSize.value = "0 MB"
                _expiredNewsCount.value = 0
            }
        }
    }

    private fun calculateDirSize(dir: File): Long {
        var size = 0L
        if (dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
                size += if (file.isDirectory) calculateDirSize(file) else file.length()
            }
        } else {
            size = dir.length()
        }
        return size
    }

    private fun formatSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }
}