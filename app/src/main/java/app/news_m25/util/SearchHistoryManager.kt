package app.news_m25.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.searchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

object SearchHistoryManager {
    private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
    private const val MAX_HISTORY_SIZE = 10

    fun getSearchHistory(context: Context): Flow<List<String>> {
        return context.searchHistoryDataStore.data.map { preferences ->
            preferences[SEARCH_HISTORY_KEY]?.split("|")?.filter { it.isNotBlank() } ?: emptyList()
        }
    }

    suspend fun addSearchQuery(context: Context, query: String) {
        if (query.isBlank()) return
        context.searchHistoryDataStore.edit { preferences ->
            val currentHistory = preferences[SEARCH_HISTORY_KEY]
                ?.split("|")
                ?.filter { it.isNotBlank() }
                ?.toMutableList()
                ?: mutableListOf()

            currentHistory.remove(query)
            currentHistory.add(0, query)

            val trimmedHistory = currentHistory.take(MAX_HISTORY_SIZE)
            preferences[SEARCH_HISTORY_KEY] = trimmedHistory.joinToString("|")
        }
        Logger.d("SearchHistoryManager", "Added search query: $query")
    }

    suspend fun removeSearchQuery(context: Context, query: String) {
        context.searchHistoryDataStore.edit { preferences ->
            val currentHistory = preferences[SEARCH_HISTORY_KEY]
                ?.split("|")
                ?.filter { it.isNotBlank() }
                ?.toMutableList()
                ?: mutableListOf()

            currentHistory.remove(query)
            preferences[SEARCH_HISTORY_KEY] = currentHistory.joinToString("|")
        }
        Logger.d("SearchHistoryManager", "Removed search query: $query")
    }

    suspend fun clearSearchHistory(context: Context) {
        context.searchHistoryDataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY_KEY)
        }
        Logger.d("SearchHistoryManager", "Search history cleared")
    }
}