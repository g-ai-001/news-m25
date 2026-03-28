package app.news_m25.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.news_m25.data.local.dao.ReadHistoryDao
import app.news_m25.data.local.entity.ReadHistoryEntity
import app.news_m25.domain.model.News
import app.news_m25.domain.repository.NewsRepository
import app.news_m25.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsDetailUiState(
    val news: News? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val readHistoryDao: ReadHistoryDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val newsId: Long = savedStateHandle.get<Long>("newsId") ?: 0L

    private val _uiState = MutableStateFlow(NewsDetailUiState())
    val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()

    init {
        Logger.i("NewsDetailViewModel", "Loading news with id: $newsId")
        loadNews()
        incrementViewCount()
        addToHistory()
    }

    private fun loadNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val news = newsRepository.getNewsById(newsId)
                if (news != null) {
                    Logger.i("NewsDetailViewModel", "Loaded news: ${news.title}")
                    _uiState.value = _uiState.value.copy(
                        news = news,
                        isLoading = false
                    )
                } else {
                    Logger.w("NewsDetailViewModel", "News not found: $newsId")
                    _uiState.value = _uiState.value.copy(
                        error = "新闻不存在",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Logger.e("NewsDetailViewModel", "Failed to load news", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    private fun incrementViewCount() {
        viewModelScope.launch {
            try {
                newsRepository.incrementViewCount(newsId)
            } catch (e: Exception) {
                Logger.e("NewsDetailViewModel", "Failed to increment view count", e)
            }
        }
    }

    private fun addToHistory() {
        viewModelScope.launch {
            try {
                readHistoryDao.insertReadHistory(ReadHistoryEntity(newsId = newsId))
                Logger.i("NewsDetailViewModel", "Added news $newsId to history")
            } catch (e: Exception) {
                Logger.e("NewsDetailViewModel", "Failed to add to history", e)
            }
        }
    }

    fun toggleFavorite() {
        val news = _uiState.value.news ?: return
        viewModelScope.launch {
            try {
                val newState = !news.isFavorite
                newsRepository.toggleFavorite(news.id, newState)
                _uiState.value = _uiState.value.copy(
                    news = news.copy(isFavorite = newState)
                )
                Logger.i("NewsDetailViewModel", "Toggled favorite to $newState")
            } catch (e: Exception) {
                Logger.e("NewsDetailViewModel", "Failed to toggle favorite", e)
            }
        }
    }
}
