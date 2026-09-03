package dev.sayed.mehrabalmomen.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.utils.isAndroid
import dev.sayed.mehrabalmomen.presentation.screen.azkar.AzkarScreen
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeScreen
import dev.sayed.mehrabalmomen.presentation.screen.prayers.PrayerTimesScreen
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method.CalculationMethodScreen
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.PermissionsScreen
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.BatteryOptimizationScreen

@Composable
fun AppNavigation(settingsRepository: SettingsRepository) {
    val navController = rememberNavController()

    val onboardingComplete by settingsRepository
        .observeOnboardingComplete()
        .collectAsState(initial = null)

    if (onboardingComplete == null) return

    val startDestination: Route =
        if (onboardingComplete == true) Route.AppRoute
        else Route.CalculationMethodScreen

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.AppRoute> {
            MainContainer(rootNavController = navController)
        }
        
        composable<Route.CalculationMethodScreen> {
            CalculationMethodScreen(
                onNavigateToPermissions = { navController.navigate(Route.PermissionsScreen) }
            )
        }
        
        composable<Route.PermissionsScreen> {
            PermissionsScreen(
                onNavigateToBatteryOptimization = { 
                    navController.navigate(Route.BatteryOptimizationScreen)
                },
                onNavigateToHome = { 
                    navController.navigate(Route.AppRoute) {
                        popUpTo(Route.CalculationMethodScreen) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Route.BatteryOptimizationScreen> {
            BatteryOptimizationScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenSettings = { /* Handled in screen */ },
                onNavigateToHome = { 
                    navController.navigate(Route.AppRoute) {
                        popUpTo(Route.CalculationMethodScreen) { inclusive = true }
                    }
                },
                onNavigateToLearnMore = { /* Handled in screen */ },
                manufacturer = "unknown"
            )
        }

        composable<Route.HomeScreen> {
             HomeScreen(
                onNavigateToPrayerTimes = { navController.navigate(Route.PrayerTimes) },
                onNavigateToCalibrateDevice = { navController.navigate(Route.CalibrateDevice) },
                onNavigateToSettings = { navController.navigate(Route.SettingsScreen) },
                onNavigateToQuran = { navController.navigate(Route.SurahListScreen) },
                onNavigateToTilawah = { surahId, arabicName, englishName, ayahId ->
                    navController.navigate(
                        Route.SurahAyatScreen(surahId, arabicName, englishName, ayahId)
                    )
                }
            )
        }
        composable<Route.PrayerTimes> {
            PrayerTimesScreen(
                onNavigateBack = { navController.popBackStack() },
                onRequestExactAlarm = {},
                onRequestNotificationPermission = {},
                onRequestIgnoreBatteryOptimization = {}
            )
        }
        composable<Route.AzkarScreen> {
            AzkarScreen(
                onNavigateToDetails = { title -> navController.navigate(Route.AzkarDetailScreen(title)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
