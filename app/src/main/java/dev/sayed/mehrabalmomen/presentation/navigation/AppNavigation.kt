package dev.sayed.mehrabalmomen.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.screen.calculation_method.CalculationMethodScreen
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.Figure8CalibrationScreen
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeScreen
import dev.sayed.mehrabalmomen.presentation.screen.location_permission.LocationPermissionScreen
import dev.sayed.mehrabalmomen.presentation.screen.madhab.MadhabScreen
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesViewScreen
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahScreen

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun AppNavigation(settingsRepository: SettingsRepository) {
    val navController = rememberNavController()

    val onboardingComplete by settingsRepository
        .observeOnboardingComplete()
        .collectAsState(initial = null)

    if (onboardingComplete == null) return

    val startDestination =
        if (onboardingComplete == true) Route.HomeScreen
        else Route.MadhabScreen

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.HomeScreen> { HomeScreen(navController) }
        composable<Route.CalibrateDevice> { Figure8CalibrationScreen(navController) }
        composable<Route.FullPrayerTimeView> { FullPrayerTimesViewScreen(navController) }
        composable<Route.QiblahScreen> { QiblahScreen(navController) }
        composable<Route.LocationPermissionScreen> { LocationPermissionScreen(navController) }
        composable<Route.MadhabScreen> { MadhabScreen(navController) }
        composable<Route.CalculationMethodScreen> { CalculationMethodScreen(navController) }
    }
}