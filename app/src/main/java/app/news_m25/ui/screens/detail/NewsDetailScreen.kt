package app.news_m25.ui.screens.detail

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.news_m25.ui.components.NewsImage
import app.news_m25.util.DateUtils
import app.news_m25.util.Logger
import app.news_m25.util.TextSize
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: Long,
    onBackClick: () -> Unit,
    viewModel: NewsDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val textSize by viewModel.textSize.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isSpeaking by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.CHINESE
                Logger.d("NewsDetailScreen", "TTS initialized successfully")
            } else {
                Logger.e("NewsDetailScreen", "TTS initialization failed")
            }
        }

        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    LaunchedEffect(uiState.readingPosition) {
        if (uiState.readingPosition > 0f && scrollState.maxValue > 0) {
            scrollState.scrollTo(uiState.readingPosition.toInt())
        }
    }

    LaunchedEffect(scrollState.value) {
        if (scrollState.maxValue > 0) {
            viewModel.saveReadingPosition(scrollState.value.toFloat())
        }
    }

    val toggleTts = {
        if (isSpeaking) {
            tts?.stop()
            isSpeaking = false
        } else {
            uiState.news?.let { news ->
                tts?.speak(news.content, TextToSpeech.QUEUE_FLUSH, null, "news_content")
                isSpeaking = true
            } ?: Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新闻详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    uiState.news?.let { news ->
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                imageVector = if (news.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "收藏",
                                tint = if (news.isFavorite) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = viewModel::toggleReadLater) {
                            Icon(
                                imageVector = if (news.isReadLater) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "稍后阅读",
                                tint = if (news.isReadLater) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = toggleTts) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "语音播报"
                            )
                        }
                        IconButton(onClick = { shareNews(context, news.title, news.summary) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "分享"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
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
                    Text(
                        text = uiState.error ?: "未知错误",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.news != null -> {
                    val news = uiState.news!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        if (news.imageUrl != null) {
                            NewsImage(
                                imageUrl = news.imageUrl,
                                contentDescription = "新闻图片",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${news.source} · ${news.author} · ${DateUtils.formatFullDate(news.publishedAt)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "阅读 ${news.viewCount + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        RatingBar(
                            rating = news.rating,
                            onRatingChange = viewModel::setRating
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        val textSizeValue = TextSize.fromValue(textSize)
                        val bodyTextStyle = when (textSizeValue) {
                            TextSize.SMALL -> MaterialTheme.typography.bodyMedium
                            TextSize.MEDIUM -> MaterialTheme.typography.bodyLarge
                            TextSize.LARGE -> MaterialTheme.typography.bodyLarge.copy(fontSize = MaterialTheme.typography.bodyLarge.fontSize * 1.2f)
                            TextSize.EXTRA_LARGE -> MaterialTheme.typography.bodyLarge.copy(fontSize = MaterialTheme.typography.bodyLarge.fontSize * 1.4f)
                        }

                        Text(
                            text = news.content,
                            style = bodyTextStyle,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = bodyTextStyle.lineHeight * 1.5
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingBar(
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "评分: ",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        (1..5).forEach { star ->
            IconButton(
                onClick = { onRatingChange(if (rating == star) 0 else star) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "评${star}星",
                    tint = if (star <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun shareNews(context: Context, title: String, summary: String) {
    try {
        val shareText = "$title\n\n$summary\n\n来自：今日头条"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "分享新闻"))
        Logger.d("NewsDetailScreen", "Share intent created for: $title")
    } catch (e: Exception) {
        Logger.e("NewsDetailScreen", "Failed to share news", e)
    }
}
