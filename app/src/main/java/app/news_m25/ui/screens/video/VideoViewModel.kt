package app.news_m25.ui.screens.video

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

data class VideoUiState(
    val videos: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val application: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    init {
        Logger.d("VideoViewModel", "VideoViewModel initialized")
        loadVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                newsRepository.getAllNews()
                    .catch { e ->
                        Logger.e("VideoViewModel", "Failed to load videos", e)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                    .collect { newsList ->
                        val videos = newsList.filter { it.videoUrl != null }
                        Logger.d("VideoViewModel", "Loaded ${videos.size} videos")
                        _uiState.value = _uiState.value.copy(
                            videos = videos,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                Logger.e("VideoViewModel", "Unexpected error", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun refresh() {
        loadVideos()
    }
}