package app.news_m25.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.news_m25.domain.model.News
import app.news_m25.util.DateUtils

@Composable
private fun NewsMetaInfo(
    source: String,
    timestamp: Long,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.labelSmall
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = source,
            style = style,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = " · ",
            style = style,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = DateUtils.formatRelativeTime(timestamp),
            style = style,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActionButtons(
    isFavorite: Boolean,
    isReadLater: Boolean,
    onFavoriteClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Int = 20
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier.size(if (iconSize == 20) 32.dp else 40.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "收藏",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(iconSize.dp)
            )
        }
        IconButton(
            onClick = onReadLaterClick,
            modifier = Modifier.size(if (iconSize == 20) 32.dp else 40.dp)
        ) {
            Icon(
                imageVector = if (isReadLater) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "稍后阅读",
                tint = if (isReadLater) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(iconSize.dp)
            )
        }
    }
}

@Composable
private fun NewsImageSection(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    if (imageUrl != null) {
        Box(modifier = modifier) {
            NewsImage(
                imageUrl = imageUrl,
                contentDescription = "新闻图片",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

@Composable
fun NewsCard(
    news: News,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            NewsImageSection(imageUrl = news.imageUrl)

            if (news.imageUrl != null) {
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = news.summary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NewsMetaInfo(
                        source = news.source,
                        timestamp = news.publishedAt
                    )

                    ActionButtons(
                        isFavorite = news.isFavorite,
                        isReadLater = news.isReadLater,
                        onFavoriteClick = onFavoriteClick,
                        onReadLaterClick = onReadLaterClick
                    )
                }
            }
        }
    }
}

@Composable
fun NewsCardLarge(
    news: News,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            NewsImage(
                imageUrl = news.imageUrl,
                contentDescription = "新闻图片",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = news.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NewsMetaInfo(
                        source = news.source,
                        timestamp = news.publishedAt,
                        style = MaterialTheme.typography.labelMedium
                    )

                    ActionButtons(
                        isFavorite = news.isFavorite,
                        isReadLater = news.isReadLater,
                        onFavoriteClick = onFavoriteClick,
                        onReadLaterClick = onReadLaterClick,
                        iconSize = 24
                    )
                }
            }
        }
    }
}
