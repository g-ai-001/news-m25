package app.news_m25.ui.screens.readlater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.news_m25.domain.model.News
import app.news_m25.domain.repository.NewsRepository
import app.news_m25.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReadLaterUiState(
    val news: List<News> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ReadLaterViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadLaterUiState())
    val uiState: StateFlow<ReadLaterUiState> = _uiState.asStateFlow()

    init {
        loadReadLaterNews()
    }

    private fun loadReadLaterNews() {
        viewModelScope.launch {
            newsRepository.getReadLaterNews()
                .catch { e ->
                    Logger.e("ReadLaterViewModel", "Failed to load read later news", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "加载失败"
                    )
                }
                .collect { news ->
                    _uiState.value = _uiState.value.copy(
                        news = news,
                        isLoading = false,
                        error = null
                    )
                    Logger.d("ReadLaterViewModel", "Loaded ${news.size} read later news")
                }
        }
    }

    fun toggleReadLater(news: News) {
        viewModelScope.launch {
            try {
                newsRepository.toggleReadLater(news.id, !news.isReadLater)
                Logger.d("ReadLaterViewModel", "Toggled read later for news: ${news.id}")
            } catch (e: Exception) {
                Logger.e("ReadLaterViewModel", "Failed to toggle read later", e)
            }
        }
    }

    fun toggleFavorite(news: News) {
        viewModelScope.launch {
            try {
                newsRepository.toggleFavorite(news.id, !news.isFavorite)
                Logger.d("ReadLaterViewModel", "Toggled favorite for news: ${news.id}")
            } catch (e: Exception) {
                Logger.e("ReadLaterViewModel", "Failed to toggle favorite", e)
            }
        }
    }
}