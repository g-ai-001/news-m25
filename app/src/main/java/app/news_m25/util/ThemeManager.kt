package app.news_m25.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ThemeManager {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    fun isDarkModeEnabled(context: Context): Flow<Boolean> {
        return context.settingsDataStore.data.map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }
    }

    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
        Logger.d("ThemeManager", "Dark mode set to: $enabled")
    }
}