package app.news_m25.ui.screens.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.news_m25.domain.model.News
import app.news_m25.domain.repository.NewsRepository
import app.news_m25.util.FavoriteManager
import app.news_m25.util.Logger
import app.news_m25.util.SearchHistoryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<News> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false,
    val searchHistory: List<String> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val favoriteManager: FavoriteManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSearchHistory()
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            SearchHistoryManager.getSearchHistory(context).collect { history ->
                _uiState.value = _uiState.value.copy(searchHistory = history)
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)

        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                results = emptyList(),
                hasSearched = false
            )
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            search(query)
        }
    }

    fun onSearch(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            SearchHistoryManager.addSearchQuery(context, query)
        }
        onQueryChange(query)
    }

    fun onHistoryItemClick(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        onSearch(query)
    }

    fun removeFromHistory(query: String) {
        viewModelScope.launch {
            SearchHistoryManager.removeSearchQuery(context, query)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            SearchHistoryManager.clearSearchHistory(context)
        }
    }

    private suspend fun search(query: String) {
        _uiState.value = _uiState.value.copy(isSearching = true)
        try {
            newsRepository.searchNews(query)
                .catch { e ->
                    Logger.e("SearchViewModel", "Search failed", e)
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        hasSearched = true
                    )
                }
                .collect { results ->
                    Logger.d("SearchViewModel", "Found ${results.size} results for: $query")
                    _uiState.value = _uiState.value.copy(
                        results = results,
                        isSearching = false,
                        hasSearched = true
                    )
                }
        } catch (e: Exception) {
            Logger.e("SearchViewModel", "Search failed", e)
            _uiState.value = _uiState.value.copy(
                isSearching = false,
                hasSearched = true
            )
        }
    }

    fun toggleFavorite(news: News) {
        favoriteManager.toggle(viewModelScope, news.id, news.isFavorite)
    }

    fun toggleReadLater(news: News) {
        viewModelScope.launch {
            try {
                newsRepository.toggleReadLater(news.id, !news.isReadLater)
                Logger.d("SearchViewModel", "Toggled read later for news: ${news.id}")
            } catch (e: Exception) {
                Logger.e("SearchViewModel", "Failed to toggle read later", e)
            }
        }
    }
}
