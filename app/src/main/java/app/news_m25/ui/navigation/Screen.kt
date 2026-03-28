package app.news_m25.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Video : Screen("video")
    data object Profile : Screen("profile")
    data object NewsDetail : Screen("news/{newsId}") {
        fun createRoute(newsId: Long) = "news/$newsId"
    }
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object Favorites : Screen("favorites")
}
