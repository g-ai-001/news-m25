package app.news_m25.ui.screens.home

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

data class HomeUiState(
    val news: List<News> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "推荐",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Logger.d("HomeViewModel", "HomeViewModel initialized")
        loadCategories()
        loadNews()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            newsRepository.getAllCategories()
                .catch { e ->
                    Logger.e("HomeViewModel", "Failed to load categories", e)
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                .collect { categories ->
                    _uiState.value = _uiState.value.copy(categories = categories)
                    Logger.d("HomeViewModel", "Loaded ${categories.size} categories")
                }
        }
    }

    fun loadNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val category = _uiState.value.selectedCategory
                Logger.d("HomeViewModel", "Loading news for category: $category")

                val newsFlow = if (category == "推荐") {
                    newsRepository.getAllNews()
                } else {
                    newsRepository.getNewsByCategory(category)
                }

                newsFlow
                    .catch { e ->
                        Logger.e("HomeViewModel", "Failed to load news", e)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                    .collect { news ->
                        Logger.d("HomeViewModel", "Loaded ${news.size} news items")
                        _uiState.value = _uiState.value.copy(
                            news = news,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                Logger.e("HomeViewModel", "Unexpected error", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun selectCategory(category: String) {
        if (_uiState.value.selectedCategory != category) {
            Logger.d("HomeViewModel", "Category changed to: $category")
            _uiState.value = _uiState.value.copy(selectedCategory = category)
            loadNews()
        }
    }

    fun toggleFavorite(news: News) {
        viewModelScope.launch {
            try {
                val newFavoriteState = !news.isFavorite
                newsRepository.toggleFavorite(news.id, newFavoriteState)
                Logger.d("HomeViewModel", "Toggled favorite for news ${news.id} to $newFavoriteState")
            } catch (e: Exception) {
                Logger.e("HomeViewModel", "Failed to toggle favorite", e)
            }
        }
    }

    fun refresh() {
        loadNews()
    }
}
