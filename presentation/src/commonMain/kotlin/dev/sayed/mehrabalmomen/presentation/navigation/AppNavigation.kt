package dev.sayed.mehrabalmomen.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository

@Composable
fun AppNavigation(settingsRepository: SettingsRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.AppRoute.route
    ) {
        composable(Route.AppRoute.route) {
            MainContainer(rootNavController = navController)
        }
    }
}
