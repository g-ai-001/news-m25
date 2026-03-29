package app.news_m25.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.news_m25.domain.model.News
import app.news_m25.domain.repository.NewsRepository
import app.news_m25.util.Logger
import app.news_m25.util.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortType(val value: Int, val label: String) {
    NEWEST(0, "最新"),
    OLDEST(1, "最早"),
    MOST_VIEWED(2, "最热"),
    RECOMMENDED(3, "推荐");

    companion object {
        fun fromValue(value: Int): SortType = entries.find { it.value == value } ?: NEWEST
    }
}

data class HomeUiState(
    val news: List<News> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "推荐",
    val sortType: SortType = SortType.NEWEST,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Logger.d("HomeViewModel", "HomeViewModel initialized")
        loadCategories()
        loadSortType()
        loadNews()
    }

    private fun loadSortType() {
        viewModelScope.launch {
            SettingsManager.getSortType(application).collect { type ->
                _uiState.value = _uiState.value.copy(sortType = SortType.fromValue(type))
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            newsRepository.getAllCategories()
                .catch { e ->
                    Logger.e("HomeViewModel", "Failed to load categories", e)
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                .collect { categories ->
                    val categoriesWithRecommend = listOf("推荐") + categories
                    _uiState.value = _uiState.value.copy(categories = categoriesWithRecommend)
                    Logger.d("HomeViewModel", "Loaded ${categoriesWithRecommend.size} categories")
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
                        val sortedNews = sortNews(news, _uiState.value.sortType)
                        Logger.d("HomeViewModel", "Loaded ${sortedNews.size} news items")
                        _uiState.value = _uiState.value.copy(
                            news = sortedNews,
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

    private suspend fun sortNews(news: List<News>, sortType: SortType): List<News> {
        return when (sortType) {
            SortType.NEWEST -> news.sortedByDescending { it.publishedAt }
            SortType.OLDEST -> news.sortedBy { it.publishedAt }
            SortType.MOST_VIEWED -> news.sortedByDescending { it.viewCount }
            SortType.RECOMMENDED -> sortByPreference(news)
        }
    }

    private suspend fun sortByPreference(news: List<News>): List<News> {
        val preferences = SettingsManager.getCategoryPreferences(application).first()
        if (preferences.isEmpty()) {
            return news.sortedByDescending { it.publishedAt }
        }
        return news.sortedByDescending { item ->
            preferences[item.category] ?: 0f
        }
    }

    fun selectCategory(category: String) {
        if (_uiState.value.selectedCategory != category) {
            Logger.d("HomeViewModel", "Category changed to: $category")
            _uiState.value = _uiState.value.copy(selectedCategory = category)
            loadNews()
        }
    }

    fun setSortType(sortType: SortType) {
        viewModelScope.launch {
            SettingsManager.setSortType(application, sortType.value)
            _uiState.value = _uiState.value.copy(sortType = sortType)
            val sortedNews = sortNews(_uiState.value.news, sortType)
            _uiState.value = _uiState.value.copy(news = sortedNews)
            Logger.d("HomeViewModel", "Sort type changed to: ${sortType.label}")
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
