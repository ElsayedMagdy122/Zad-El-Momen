package dev.sayed.mehrabalmomen.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.sayed.mehrabalmomen.design_system.component.BottomNavigationBar
import dev.sayed.mehrabalmomen.design_system.component.NavItem
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.azkar.AzkarScreen
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeScreen
import dev.sayed.mehrabalmomen.presentation.screen.prayers.PrayerTimesScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zad_el_momen.presentation.generated.resources.Res
import zad_el_momen.presentation.generated.resources.azkar
import zad_el_momen.presentation.generated.resources.home
import zad_el_momen.presentation.generated.resources.ic_azkar_not_selected
import zad_el_momen.presentation.generated.resources.ic_azkar_selected
import zad_el_momen.presentation.generated.resources.ic_home_not_selected
import zad_el_momen.presentation.generated.resources.ic_home_selected
import zad_el_momen.presentation.generated.resources.ic_prayer_times_not_selected
import zad_el_momen.presentation.generated.resources.ic_prayer_times_selected
import zad_el_momen.presentation.generated.resources.ic_radio_not_selected
import zad_el_momen.presentation.generated.resources.ic_radio_selected
import zad_el_momen.presentation.generated.resources.ic_settings_not_selected
import zad_el_momen.presentation.generated.resources.ic_settings_selected
import zad_el_momen.presentation.generated.resources.prayer
import zad_el_momen.presentation.generated.resources.radio
import zad_el_momen.presentation.generated.resources.settings

@Composable
fun MainContainer(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {

    val bottomNavController = rememberNavController()

    val navItems = listOf(
        Route.HomeScreen,
        Route.PrayerTimes,
        Route.AzkarScreen,
        // Route.RadioScreen,
        // Route.SettingsScreen
    )

    val bottomItems = listOf(
        NavItem(
            title = stringResource(Res.string.home),
            selectedIcon = painterResource(Res.drawable.ic_home_selected),
            unselectedIcon = painterResource(Res.drawable.ic_home_not_selected)
        ),
        NavItem(
            title = stringResource(Res.string.prayer),
            selectedIcon = painterResource(Res.drawable.ic_prayer_times_selected),
            unselectedIcon = painterResource(Res.drawable.ic_prayer_times_not_selected)
        ),
        NavItem(
            title = stringResource(Res.string.azkar),
            selectedIcon = painterResource(Res.drawable.ic_azkar_selected),
            unselectedIcon = painterResource(Res.drawable.ic_azkar_not_selected)
        ),
        /*
        NavItem(
            title = stringResource(Res.string.radio),
            selectedIcon = painterResource(Res.drawable.ic_radio_selected),
            unselectedIcon = painterResource(Res.drawable.ic_radio_not_selected)
        ),
        NavItem(
            title = stringResource(Res.string.settings),
            selectedIcon = painterResource(Res.drawable.ic_settings_selected),
            unselectedIcon = painterResource(Res.drawable.ic_settings_not_selected)
        )
         */
    )

    val currentBackStack by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val selectedIndex = navItems.indexOfFirst { it::class.qualifiedName == currentRoute }.coerceAtLeast(0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        NavHost(
            navController = bottomNavController,
            startDestination = Route.HomeScreen,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 74.dp)
        ) {
            composable<Route.HomeScreen> { 
                HomeScreen(
                    onNavigateToPrayerTimes = { rootNavController.navigate(Route.PrayerTimes) },
                    onNavigateToCalibrateDevice = { rootNavController.navigate(Route.CalibrateDevice) },
                    onNavigateToSettings = { rootNavController.navigate(Route.SettingsScreen) },
                    onNavigateToQuran = { rootNavController.navigate(Route.SurahListScreen) },
                    onNavigateToTilawah = { surahId, arabicName, englishName, ayahId ->
                        rootNavController.navigate(
                            Route.SurahAyatScreen(surahId, arabicName, englishName, ayahId)
                        )
                    }
                ) 
            }
            composable<Route.PrayerTimes> { 
                PrayerTimesScreen(
                    onNavigateBack = { bottomNavController.popBackStack() }
                ) 
            }
            composable<Route.AzkarScreen> { 
                AzkarScreen(
                    onNavigateToDetails = { title -> rootNavController.navigate(Route.AzkarDetailScreen(title)) },
                    onNavigateBack = { bottomNavController.popBackStack() }
                ) 
            }
            // composable<Route.RadioScreen> { RadioScreen(rootNavController) }
            // composable<Route.SettingsScreen> { SettingsScreen(rootNavController) }
        }

        BottomNavigationBar(
            items = bottomItems,
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                val route = navItems[index]
                bottomNavController.navigate(route) {
                    popUpTo(bottomNavController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // CompanionOverlay()
    }
}
