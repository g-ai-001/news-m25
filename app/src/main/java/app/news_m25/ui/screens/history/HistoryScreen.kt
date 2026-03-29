package app.news_m25.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun HistoryScreen(
    onBackClick: () -> Unit,
    onNewsClick: (Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ScreenTopBar(
                title = "阅读历史",
                onBackClick = onBackClick,
                actions = {
                    if (uiState.history.isNotEmpty()) {
                        IconButton(onClick = viewModel::clearHistory) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "清空历史"
                            )
                        }
                    }
                }
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

                uiState.history.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.History,
                        title = "暂无阅读历史",
                        subtitle = "浏览新闻后会自动记录",
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
                            items = uiState.history,
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