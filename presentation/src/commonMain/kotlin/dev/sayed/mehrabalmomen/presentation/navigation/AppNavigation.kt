package dev.sayed.mehrabalmomen.presentation.navigation

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method.CalculationMethodScreen
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.PermissionsScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.SurahAyatScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList.SurahListScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.RecitersScreen
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search.RecitersSearchScreen
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

    if (onboardingComplete == null) return

    val startDestination: Route =
        if (onboardingComplete == true) Route.AppRoute
        else Route.CalculationMethodScreen

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
        composable<Route.PrayerTimes> { Box(modifier = Modifier.fillMaxSize()) }
        composable<Route.CalibrateDevice> { Box(modifier = Modifier.fillMaxSize()) }
        composable<Route.ReminderSettingsScreen> { Box(modifier = Modifier.fillMaxSize()) }
        composable<Route.AzkarScreen> { Box(modifier = Modifier.fillMaxSize()) }
        composable<Route.RadioScreen> { Box(modifier = Modifier.fillMaxSize()) }
        composable<Route.BookmarksListScreen> { Box(modifier = Modifier.fillMaxSize()) }
        composable<Route.SearchAyahScreen> { Box(modifier = Modifier.fillMaxSize()) }
        composable<Route.RecitersScreen> { RecitersScreen(navController = navController) }
        composable<Route.RecitersSearchScreen> { RecitersSearchScreen(navController = navController) }
        composable<Route.AppRoute> {
            MainContainer(rootNavController = navController)
        }
    }
}
