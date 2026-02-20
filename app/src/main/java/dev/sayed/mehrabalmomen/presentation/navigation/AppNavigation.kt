package dev.sayed.mehrabalmomen.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails.AzkarDetailScreen
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.ReportBugScreen
import dev.sayed.mehrabalmomen.presentation.screen.SearchAyah.SearchAyahScreen
import dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.SurahAyatScreen
import dev.sayed.mehrabalmomen.presentation.screen.azkar.AzkarScreen
import dev.sayed.mehrabalmomen.presentation.screen.bookmarks.BookmarksListScreen
import dev.sayed.mehrabalmomen.presentation.screen.calculation_method.CalculationMethodScreen
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.Figure8CalibrationScreen
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeScreen
import dev.sayed.mehrabalmomen.presentation.screen.location_permission.LocationPermissionScreen
import dev.sayed.mehrabalmomen.presentation.screen.madhab.MadhabScreen
import dev.sayed.mehrabalmomen.presentation.screen.maps.MapsScreen
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesViewScreen
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahListScreen
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsScreen

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
        composable<Route.SurahListScreen> { SurahListScreen(navController) }
        composable<Route.HomeScreen> { HomeScreen(navController) }
        composable<Route.CalibrateDevice> { Figure8CalibrationScreen(navController) }
        composable<Route.FullPrayerTimeView> { FullPrayerTimesViewScreen(navController) }
        composable<Route.QiblahScreen> { QiblahScreen(navController) }
        composable<Route.LocationPermissionScreen> { LocationPermissionScreen(navController) }
        composable<Route.MadhabScreen> { MadhabScreen(navController) }
        composable<Route.CalculationMethodScreen> { CalculationMethodScreen(navController) }
        composable<Route.SettingsScreen> { SettingsScreen(navController) }
        composable<Route.MapsScreen> { MapsScreen(navController) }
        composable<Route.AzkarScreen> { AzkarScreen(navController) }
        composable<Route.AzkarDetailScreen> { entry ->
            val args = entry.toRoute<Route.AzkarDetailScreen>()
            AzkarDetailScreen(
                title = args.title,
                navController = navController
            )
        }
        composable<Route.SurahAyatScreen> { entry ->
            val surah = entry.toRoute<Route.SurahAyatScreen>()
            SurahAyatScreen(
                navController = navController,
                surahId = surah.surahId,
                arabicName = surah.arabicName,
                englishName = surah.englishName
            )
        }
        composable<Route.SearchAyahScreen> { entry ->
            val args = entry.toRoute<Route.SearchAyahScreen>()
            SearchAyahScreen(
                navController = navController,
                searchType = args.type,
                surahId = args.surahId,
                surahName = args.surahName
            )
        }
        composable<Route.ReportBugScreen> {
            ReportBugScreen(navController = navController)
        }
        composable<Route.BookmarksListScreen> {
            BookmarksListScreen(navController = navController)
        }
    }
}