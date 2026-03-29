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
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideoPlayerState(
    val video: News? = null,
    val isPlaying: Boolean = false,
    val isFullscreen: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L
)

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val application: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VideoPlayerState())
    val uiState: StateFlow<VideoPlayerState> = _uiState.asStateFlow()

    fun loadVideo(videoId: Long) {
        viewModelScope.launch {
            try {
                newsRepository.getNewsById(videoId)?.let { video ->
                    _uiState.value = _uiState.value.copy(video = video)
                    Logger.d("VideoPlayerViewModel", "Loaded video: ${video.title}")
                }
            } catch (e: Exception) {
                Logger.e("VideoPlayerViewModel", "Failed to load video", e)
            }
        }
    }

    fun setPlaying(isPlaying: Boolean) {
        _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
    }

    fun setFullscreen(isFullscreen: Boolean) {
        _uiState.value = _uiState.value.copy(isFullscreen = isFullscreen)
    }

    fun updatePosition(position: Long) {
        _uiState.value = _uiState.value.copy(currentPosition = position)
    }

    fun updateDuration(duration: Long) {
        _uiState.value = _uiState.value.copy(duration = duration)
    }
}