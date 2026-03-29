package app.news_m25.data.local.entity

import app.news_m25.domain.model.News

fun NewsEntity.toDomain(): News {
    return News(
        id = id,
        title = title,
        summary = summary,
        content = content,
        category = category,
        source = source,
        author = author,
        publishedAt = publishedAt,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        isFavorite = isFavorite,
        viewCount = viewCount
    )
}

fun News.toEntity(): NewsEntity {
    return NewsEntity(
        id = id,
        title = title,
        summary = summary,
        content = content,
        category = category,
        source = source,
        author = author,
        publishedAt = publishedAt,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        isFavorite = isFavorite,
        viewCount = viewCount
    )
}
