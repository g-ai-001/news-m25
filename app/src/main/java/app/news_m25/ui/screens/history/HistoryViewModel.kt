package app.news_m25.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.news_m25.data.local.dao.ReadHistoryDao
import app.news_m25.data.local.entity.ReadHistoryEntity
import app.news_m25.data.local.entity.toDomain
import app.news_m25.domain.model.News
import app.news_m25.domain.repository.NewsRepository
import app.news_m25.util.FavoriteManager
import app.news_m25.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val history: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val readHistoryDao: ReadHistoryDao,
    private val newsRepository: NewsRepository,
    private val favoriteManager: FavoriteManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        Logger.d("HistoryViewModel", "HistoryViewModel initialized")
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            readHistoryDao.getReadHistory()
                .catch { e ->
                    Logger.e("HistoryViewModel", "Failed to load history", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { entities ->
                    val history = entities.map { it.toDomain() }
                    Logger.d("HistoryViewModel", "Loaded ${history.size} history items")
                    _uiState.value = _uiState.value.copy(
                        history = history,
                        isLoading = false
                    )
                }
        }
    }

    fun addToHistory(newsId: Long) {
        viewModelScope.launch {
            try {
                readHistoryDao.insertReadHistory(ReadHistoryEntity(newsId = newsId))
                Logger.d("HistoryViewModel", "Added news $newsId to history")
            } catch (e: Exception) {
                Logger.e("HistoryViewModel", "Failed to add to history", e)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                readHistoryDao.clearHistory()
                Logger.d("HistoryViewModel", "Cleared history")
                _uiState.value = _uiState.value.copy(history = emptyList())
            } catch (e: Exception) {
                Logger.e("HistoryViewModel", "Failed to clear history", e)
            }
        }
    }

    fun toggleFavorite(news: News) {
        favoriteManager.toggle(viewModelScope, news.id, news.isFavorite)
    }
}