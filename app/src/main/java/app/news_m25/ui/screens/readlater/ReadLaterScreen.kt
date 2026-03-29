package app.news_m25.ui.screens.readlater

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun ReadLaterScreen(
    onBackClick: () -> Unit,
    onNewsClick: (Long) -> Unit,
    viewModel: ReadLaterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ScreenTopBar(
                title = "稍后阅读",
                onBackClick = onBackClick,
                modifier = Modifier.statusBarsPadding()
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
                        message = uiState.error ?: "未知错误",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.news.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.Bookmark,
                        title = "暂无待读新闻",
                        subtitle = "点击新闻详情页的\"稍后阅读\"按钮添加",
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
                            items = uiState.news,
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