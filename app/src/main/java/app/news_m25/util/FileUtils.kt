package app.news_m25.util

import java.io.File

object FileUtils {
    fun calculateDirSize(dir: File?): Long {
        if (dir == null) return 0L
        var size = 0L
        if (dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
                size += if (file.isDirectory) calculateDirSize(file) else file.length()
            }
        } else {
            size = dir.length()
        }
        return size
    }

    fun formatSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }

    fun clearCacheDirs(cacheDir: File?, externalCacheDir: File?): Long {
        val size = calculateDirSize(cacheDir) + calculateDirSize(externalCacheDir)
        cacheDir?.deleteRecursively()
        externalCacheDir?.deleteRecursively()
        return size
    }
}