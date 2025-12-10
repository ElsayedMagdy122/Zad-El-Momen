package dev.sayed.mehrabalmomen.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.Figure8CalibrationScreen
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeScreen
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesViewScreen
@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.HomeScreen
    ) {
        composable <Route.HomeScreen>{ HomeScreen(navController) }
        composable<Route.CalibrateDevice> { Figure8CalibrationScreen(navController) }
        composable<Route.FullPrayerTimeView> { FullPrayerTimesViewScreen(navController) }
    }
}