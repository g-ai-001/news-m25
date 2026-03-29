package app.news_m25.ui.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import app.news_m25.data.local.dao.ReadHistoryDao
import app.news_m25.data.local.entity.ReadHistoryEntity
import app.news_m25.domain.model.News
import app.news_m25.domain.repository.NewsRepository
import app.news_m25.util.FavoriteManager
import app.news_m25.util.Logger
import app.news_m25.util.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsDetailUiState(
    val news: News? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    private val application: Application,
    private val newsRepository: NewsRepository,
    private val readHistoryDao: ReadHistoryDao,
    private val favoriteManager: FavoriteManager,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val newsId: Long = savedStateHandle.get<Long>("newsId") ?: 0L

    private val _uiState = MutableStateFlow(NewsDetailUiState())
    val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()

    val textSize: StateFlow<Int> = SettingsManager.getTextSize(application)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1
        )

    init {
        Logger.d("NewsDetailViewModel", "Loading news with id: $newsId")
        loadNews()
    }

    private fun loadNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val news = newsRepository.getNewsById(newsId)
                if (news != null) {
                    Logger.d("NewsDetailViewModel", "Loaded news: ${news.title}")
                    _uiState.value = _uiState.value.copy(
                        news = news,
                        isLoading = false
                    )
                    incrementViewCount()
                    addToHistory()
                    incrementReadCount()
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
                Logger.d("NewsDetailViewModel", "Added news $newsId to history")
            } catch (e: Exception) {
                Logger.e("NewsDetailViewModel", "Failed to add to history", e)
            }
        }
    }

    private fun incrementReadCount() {
        viewModelScope.launch {
            try {
                SettingsManager.incrementReadCount(application)
                SettingsManager.addReadTime(application, 1)
                _uiState.value.news?.let { news ->
                    SettingsManager.incrementCategoryViewCount(application, news.category)
                }
                Logger.d("NewsDetailViewModel", "Incremented read count and added read time")
            } catch (e: Exception) {
                Logger.e("NewsDetailViewModel", "Failed to update read stats", e)
            }
        }
    }

    fun toggleFavorite() {
        val news = _uiState.value.news ?: return
        favoriteManager.toggle(viewModelScope, news.id, news.isFavorite) { e ->
            Logger.e("NewsDetailViewModel", "Failed to toggle favorite", e)
        }
        _uiState.value = _uiState.value.copy(
            news = news.copy(isFavorite = !news.isFavorite)
        )
    }
}
