package dev.sayed.mehrabalmomen.presentation.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.LocalIsDarkTheme
import dev.sayed.mehrabalmomen.presentation.base.setAppLocale
import dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails.AzkarDetailScreen
import dev.sayed.mehrabalmomen.presentation.screen.SearchAyah.SearchAyahScreen
import dev.sayed.mehrabalmomen.presentation.screen.SearchAyah.SearchType
import dev.sayed.mehrabalmomen.presentation.screen.azkar.AzkarScreen
import dev.sayed.mehrabalmomen.presentation.screen.bookmarks.BookmarksListScreen
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.Figure8CalibrationScreen
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method.CalculationMethodScreen
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.PermissionsScreen
import dev.sayed.mehrabalmomen.presentation.screen.prayers.PrayerTimesScreen
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.SurahAyatScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList.SurahListScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.RecitersScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search.RecitersSearchScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.Tasmee3Screen
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioScreen
import dev.sayed.mehrabalmomen.presentation.screen.reminders.ReminderSettingsScreen
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsScreen
import dev.sayed.mehrabalmomen.presentation.screen.settings.contact_us.ContactUsScreen
import dev.sayed.mehrabalmomen.presentation.screen.settings.faq.FAQScreen
import dev.sayed.mehrabalmomen.presentation.screen.settings.privacy.PrivacyAnPolicyScreen

@Composable
fun AppNavigation(settingsRepository: SettingsRepository) {
    val navController = rememberNavController()

    val onboardingComplete by settingsRepository
        .observeOnboardingComplete()
        .collectAsState(initial = null)

    val appSettings by settingsRepository
        .observeAppSettings()
        .collectAsState(initial = null)

    if (onboardingComplete == null || appSettings == null) return

    val isDarkTheme = when (appSettings?.theme) {
        AppSettings.Theme.DARK -> true
        AppSettings.Theme.LIGHT -> false
        else -> isSystemInDarkTheme()
    }
    val language = appSettings?.language ?: AppSettings.Language.ARABIC

    LaunchedEffect(language) {
        setAppLocale(language)
    }

    val startDestination: Route =
        if (onboardingComplete == true) Route.AppRoute
        else Route.CalculationMethodScreen

    CompositionLocalProvider(
        LocalAppLocale provides language,
        LocalIsDarkTheme provides isDarkTheme
    ) {
        MehrabTheme(
            language = language,
            isDarkTheme = isDarkTheme
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable<Route.CalculationMethodScreen> { CalculationMethodScreen(navController) }
                composable<Route.PermissionsScreen> { PermissionsScreen(navController) }
                composable<Route.SurahListScreen> { SurahListScreen(navController) }
                composable<Route.SurahAyatScreen> { entry ->
                    val surah = entry.toRoute<Route.SurahAyatScreen>()
                    SurahAyatScreen(
                        surahId = surah.surahId,
                        arabicName = surah.arabicName,
                        englishName = surah.englishName,
                        navController = navController
                    )
                }
                composable<Route.SettingsScreen> { SettingsScreen(navController) }
                composable<Route.ContactUsScreen> { ContactUsScreen(navController) }
                composable<Route.FAQScreen> { FAQScreen(navController) }
                composable<Route.PrivacyScreen> { PrivacyAnPolicyScreen(navController) }
                composable<Route.PrayerTimes> { PrayerTimesScreen(navController = navController) }
                composable<Route.QiblahScreen> { QiblahScreen(navController) }
                composable<Route.CalibrateDevice> { Figure8CalibrationScreen(navController) }
                composable<Route.ReminderSettingsScreen> { ReminderSettingsScreen(navController = navController) }
                composable<Route.AzkarScreen> { AzkarScreen(navController) }
                composable<Route.RadioScreen> { RadioScreen(navController) }
                composable<Route.AzkarDetailScreen> { entry ->
                    val route = entry.toRoute<Route.AzkarDetailScreen>()
                    AzkarDetailScreen(title = route.title, navController = navController)
                }
                composable<Route.BookmarksListScreen> {
                    BookmarksListScreen(navController = navController)
                }
                composable<Route.SearchAyahScreen> { entry ->
                    val args = entry.toRoute<Route.SearchAyahScreen>()
                    SearchAyahScreen(
                        navController = navController,
                        searchType = runCatching { SearchType.valueOf(args.type) }.getOrDefault(SearchType.QURAN),
                        surahId = args.surahId,
                        surahName = args.surahName
                    )
                }
                composable<Route.Tasmee3Screen> { entry ->
                    val args = entry.toRoute<Route.Tasmee3Screen>()
                    Tasmee3Screen(
                        surahId = args.surahId,
                        surahName = args.surahName,
                        startAyah = args.startAyah,
                        endAyah = args.endAyah,
                        navController = navController
                    )
                }
                composable<Route.RecitersScreen> { RecitersScreen(navController = navController) }
                composable<Route.RecitersSearchScreen> { RecitersSearchScreen(navController = navController) }
                composable<Route.AppRoute> {
                    MainContainer(rootNavController = navController)
                }
            }
        }
    }
}
