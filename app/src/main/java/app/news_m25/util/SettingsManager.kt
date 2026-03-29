package app.news_m25.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object SettingsManager {
    private val TEXT_SIZE_KEY = intPreferencesKey("text_size")
    private val READ_COUNT_KEY = intPreferencesKey("read_count")
    private val READ_TIME_KEY = longPreferencesKey("read_time_minutes")
    private val SORT_TYPE_KEY = intPreferencesKey("sort_type")
    private val CATEGORY_PREFIX_KEY = "category_view_"

    fun getTextSize(context: Context): Flow<Int> {
        return context.settingsDataStore.data.map { preferences ->
            preferences[TEXT_SIZE_KEY] ?: 1
        }
    }

    suspend fun setTextSize(context: Context, size: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[TEXT_SIZE_KEY] = size
        }
        Logger.d("SettingsManager", "Text size set to: $size")
    }

    fun getReadCount(context: Context): Flow<Int> {
        return context.settingsDataStore.data.map { preferences ->
            preferences[READ_COUNT_KEY] ?: 0
        }
    }

    suspend fun incrementReadCount(context: Context) {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[READ_COUNT_KEY] ?: 0
            preferences[READ_COUNT_KEY] = current + 1
        }
    }

    fun getReadTimeMinutes(context: Context): Flow<Long> {
        return context.settingsDataStore.data.map { preferences ->
            preferences[READ_TIME_KEY] ?: 0L
        }
    }

    suspend fun addReadTime(context: Context, minutes: Long) {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[READ_TIME_KEY] ?: 0L
            preferences[READ_TIME_KEY] = current + minutes
        }
        Logger.d("SettingsManager", "Added $minutes minutes to read time")
    }

    fun getSortType(context: Context): Flow<Int> {
        return context.settingsDataStore.data.map { preferences ->
            preferences[SORT_TYPE_KEY] ?: 0
        }
    }

    suspend fun setSortType(context: Context, type: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[SORT_TYPE_KEY] = type
        }
        Logger.d("SettingsManager", "Sort type set to: $type")
    }

    suspend fun clearAll(context: Context) {
        context.settingsDataStore.edit { preferences ->
            preferences.clear()
        }
        Logger.d("SettingsManager", "All settings cleared")
    }

    fun getCategoryViewCounts(context: Context): Flow<Map<String, Int>> {
        return context.settingsDataStore.data.map { preferences ->
            preferences.asMap()
                .filterKeys { it.name.startsWith(CATEGORY_PREFIX_KEY) }
                .mapKeys { it.key.name.removePrefix(CATEGORY_PREFIX_KEY) }
                .mapValues { (it.value as? Int) ?: 0 }
        }
    }

    suspend fun incrementCategoryViewCount(context: Context, category: String) {
        context.settingsDataStore.edit { preferences ->
            val key = intPreferencesKey(CATEGORY_PREFIX_KEY + category)
            val current = preferences[key] ?: 0
            preferences[key] = current + 1
        }
        Logger.d("SettingsManager", "Incremented view count for category: $category")
    }

    fun getCategoryPreferences(context: Context): Flow<Map<String, Float>> {
        return getCategoryViewCounts(context).map { viewCounts ->
            val total = viewCounts.values.sum().toFloat()
            if (total > 0) {
                viewCounts.mapValues { it.value.toFloat() / total }
            } else {
                emptyMap()
            }
        }
    }
}

enum class TextSize(val value: Int, val label: String) {
    SMALL(0, "小"),
    MEDIUM(1, "中"),
    LARGE(2, "大"),
    EXTRA_LARGE(3, "特大");

    companion object {
        fun fromValue(value: Int): TextSize = entries.find { it.value == value } ?: MEDIUM
    }
}
