package app.news_m25.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue

object Logger {
    private val logQueue = ConcurrentLinkedQueue<String>()
    private var logFile: File? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun init(context: Context) {
        val logDir = context.getExternalFilesDir(null) ?: return
        if (!logDir.exists()) logDir.mkdirs()
        logFile = File(logDir, "news-m25-${fileNameFormat.format(Date())}.log")
    }

    fun d(tag: String, message: String) {
        log("DEBUG", tag, message)
    }

    fun i(tag: String, message: String) {
        log("INFO", tag, message)
    }

    fun w(tag: String, message: String) {
        log("WARN", tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        log("ERROR", tag, message)
        throwable?.let {
            log("ERROR", tag, it.stackTraceToString())
        }
    }

    private fun log(level: String, tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] [$level] [$tag] $message"
        logQueue.offer(logEntry)
        flushLogs()
    }

    private fun flushLogs() {
        val file = logFile ?: return
        try {
            FileWriter(file, true).use { writer ->
                while (true) {
                    val entry = logQueue.poll() ?: break
                    writer.write(entry)
                    writer.write("\n")
                }
            }
        } catch (_: Exception) {
        }
    }
}
