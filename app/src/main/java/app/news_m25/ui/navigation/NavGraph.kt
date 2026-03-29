package app.news_m25.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import app.news_m25.ui.screens.detail.NewsDetailScreen
import app.news_m25.ui.screens.favorites.FavoritesScreen
import app.news_m25.ui.screens.history.HistoryScreen
import app.news_m25.ui.screens.home.HomeScreen
import app.news_m25.ui.screens.profile.ProfileScreen
import app.news_m25.ui.screens.search.SearchScreen
import app.news_m25.ui.screens.settings.SettingsScreen
import app.news_m25.ui.screens.video.VideoPlayerScreen
import app.news_m25.ui.screens.video.VideoScreen

@Composable
fun NewsNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNewsClick = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        composable(Screen.Video.route) {
            VideoScreen(
                onVideoClick = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onFavoritesClick = {
                    navController.navigate(Screen.Favorites.route)
                },
                onHistoryClick = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        composable(
            route = Screen.NewsDetail.route,
            arguments = listOf(
                navArgument("newsId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getLong("newsId") ?: 0L
            NewsDetailScreen(
                newsId = newsId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.VideoPlayer.route,
            arguments = listOf(
                navArgument("videoId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getLong("videoId") ?: 0L
            VideoPlayerScreen(
                videoId = videoId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNewsClick = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBackClick = { navController.popBackStack() },
                onNewsClick = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onBackClick = { navController.popBackStack() },
                onNewsClick = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                }
            )
        }
    }
}
