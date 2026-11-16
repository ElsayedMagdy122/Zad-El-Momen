package dev.sayed.mehrabalmomen.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method.CalculationMethodScreen
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.PermissionsScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList.SurahListScreen

@Composable
fun AppNavigation(settingsRepository: SettingsRepository) {
    val navController = rememberNavController()

    val onboardingComplete by settingsRepository
        .observeOnboardingComplete()
        .collectAsState(initial = null)

    if (onboardingComplete == null) return

    val startDestination =
        if (onboardingComplete == true) Route.AppRoute.route
        else Route.CalculationMethodScreen.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Route.CalculationMethodScreen.route) { CalculationMethodScreen(navController) }
        composable(Route.PermissionsScreen.route) { PermissionsScreen(navController) }
        composable(Route.SurahListScreen.route) { SurahListScreen(navController) }
        composable(Route.AppRoute.route) {
            MainContainer(rootNavController = navController)
        }
    }
}
