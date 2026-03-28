package app.news_m25.ui.screens.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.news_m25.util.Logger
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
    application: Application
) : AndroidViewModel(application) {

    val isDarkMode: StateFlow<Boolean> = ThemeManager.isDarkModeEnabled(application)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val _cacheSize = MutableStateFlow("0 MB")
    val cacheSize: StateFlow<String> = _cacheSize.asStateFlow()

    init {
        viewModelScope.launch {
            updateCacheSize()
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            ThemeManager.setDarkMode(getApplication(), enabled)
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

                    Logger.d("SettingsViewModel", "Cleared cache: ${formatSize(totalSize)}")
                    updateCacheSize()
                } catch (e: Exception) {
                    Logger.e("SettingsViewModel", "Failed to clear cache", e)
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
                    clearCache()

                    // Clear shared preferences
                    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()

                    // Reset theme to default
                    ThemeManager.setDarkMode(context, false)

                    Logger.d("SettingsViewModel", "All data cleared")
                    updateCacheSize()
                } catch (e: Exception) {
                    Logger.e("SettingsViewModel", "Failed to clear all data", e)
                }
            }
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