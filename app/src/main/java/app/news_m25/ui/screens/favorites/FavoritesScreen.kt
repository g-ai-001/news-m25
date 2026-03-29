package app.news_m25.ui.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.news_m25.ui.components.EmptyState
import app.news_m25.ui.components.ErrorState
import app.news_m25.ui.components.NewsCard
import app.news_m25.ui.components.ScreenTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onNewsClick: (Long) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ScreenTopBar(
                title = "我的收藏",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error ?: "加载失败",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.favorites.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.FavoriteBorder,
                        title = "暂无收藏",
                        subtitle = "浏览新闻时点击收藏按钮添加",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.favorites,
                            key = { it.id }
                        ) { news ->
                            NewsCard(
                                news = news,
                                onClick = { onNewsClick(news.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(news) },
                                onReadLaterClick = { viewModel.toggleReadLater(news) }
                            )
                        }
                    }
                }
            }
        }
    }
}