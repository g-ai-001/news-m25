package app.news_m25.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Video : Screen("video")
    data object Profile : Screen("profile")
    data object NewsDetail : Screen("news/{newsId}") {
        fun createRoute(newsId: Long) = "news/$newsId"
    }
    data object VideoPlayer : Screen("video/{videoId}") {
        fun createRoute(videoId: Long) = "video/$videoId"
    }
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object Favorites : Screen("favorites")
    data object History : Screen("history")
    data object ReadLater : Screen("readlater")
    data object ImageGallery : Screen("gallery?urls={urls}&index={index}") {
        fun createRoute(imageUrls: List<String>, index: Int = 0): String {
            val encodedUrls = imageUrls.joinToString(",")
            return "gallery?urls=$encodedUrls&index=$index"
        }
    }
}
