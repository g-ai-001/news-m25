package app.news_m25.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import app.news_m25.MainActivity
import app.news_m25.data.local.database.NewsDatabase
import app.news_m25.data.local.entity.NewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class NewsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val news = withContext(Dispatchers.IO) {
            try {
                val database = NewsDatabase.getDatabase(context)
                val newsDao = database.newsDao()
                newsDao.getAllNews().first().take(5)
            } catch (e: Exception) {
                emptyList()
            }
        }

        provideContent {
            GlanceTheme {
                NewsWidgetContent(news = news, context = context)
            }
        }
    }
}

@Composable
private fun NewsWidgetContent(news: List<NewsEntity>, context: Context) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(android.graphics.Color.WHITE))
            .padding(12.dp)
            .cornerRadius(16.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "今日头条",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(android.graphics.Color.parseColor("#FF6B6B"))
                )
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(
                text = "最新资讯",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = ColorProvider(android.graphics.Color.GRAY)
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        if (news.isEmpty()) {
            Text(
                text = "暂无新闻",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = ColorProvider(android.graphics.Color.GRAY)
                )
            )
        } else {
            news.take(4).forEachIndexed { index, item ->
                if (index > 0) {
                    Spacer(modifier = GlanceModifier.height(6.dp))
                }
                NewsItem(newsItem = item, context = context)
            }
        }
    }
}

@Composable
private fun NewsItem(newsItem: NewsEntity, context: Context) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("newsId", newsItem.id)
    }

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(ColorProvider(android.graphics.Color.parseColor("#F5F5F5")))
            .padding(8.dp)
            .cornerRadius(8.dp)
            .clickable(actionStartActivity(intent))
    ) {
        Text(
            text = newsItem.title,
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ColorProvider(android.graphics.Color.parseColor("#333333"))
            ),
            maxLines = 2
        )
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = "${newsItem.source} · ${formatTimeAgo(newsItem.publishedAt)}",
            style = TextStyle(
                fontSize = 10.sp,
                color = ColorProvider(android.graphics.Color.GRAY)
            )
        )
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / 60000
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "刚刚"
        minutes < 60 -> "${minutes}分钟前"
        hours < 24 -> "${hours}小时前"
        days < 7 -> "${days}天前"
        else -> "${days / 7}周前"
    }
}

class NewsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NewsWidget()
}