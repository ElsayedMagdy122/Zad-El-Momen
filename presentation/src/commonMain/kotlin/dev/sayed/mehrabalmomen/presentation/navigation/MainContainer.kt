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
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeScreen
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsScreen
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.azan
import zadelmomen.presentation.generated.resources.azkar
import zadelmomen.presentation.generated.resources.home
import zadelmomen.presentation.generated.resources.ic_azan_not_selected
import zadelmomen.presentation.generated.resources.ic_azan_selected
import zadelmomen.presentation.generated.resources.ic_azkar_not_selected
import zadelmomen.presentation.generated.resources.ic_azkar_selected
import zadelmomen.presentation.generated.resources.ic_home_not_selected
import zadelmomen.presentation.generated.resources.ic_home_selected
import zadelmomen.presentation.generated.resources.ic_radio_not_selected
import zadelmomen.presentation.generated.resources.ic_radio_selected
import zadelmomen.presentation.generated.resources.ic_settings_not_selected
import zadelmomen.presentation.generated.resources.ic_settings_selected
import zadelmomen.presentation.generated.resources.radio
import zadelmomen.presentation.generated.resources.settings

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
        Route.RadioScreen,
        Route.SettingsScreen
    )

    val bottomItems = listOf(
        NavItem(
            title = localizedString(Res.string.home),
            selectedIcon = painterResource(Res.drawable.ic_home_selected),
            unselectedIcon = painterResource(Res.drawable.ic_home_not_selected)
        ),
        NavItem(
            title = localizedString(Res.string.azan),
            selectedIcon = painterResource(Res.drawable.ic_azan_selected),
            unselectedIcon = painterResource(Res.drawable.ic_azan_not_selected)
        ),
        NavItem(
            title = localizedString(Res.string.azkar),
            selectedIcon = painterResource(Res.drawable.ic_azkar_selected),
            unselectedIcon = painterResource(Res.drawable.ic_azkar_not_selected)
        ),
        NavItem(
            title = localizedString(Res.string.radio),
            selectedIcon = painterResource(Res.drawable.ic_radio_selected),
            unselectedIcon = painterResource(Res.drawable.ic_radio_not_selected)
        ),
        NavItem(
            title = localizedString(Res.string.settings),
            selectedIcon = painterResource(Res.drawable.ic_settings_selected),
            unselectedIcon = painterResource(Res.drawable.ic_settings_not_selected)
        )
    )

    val currentBackStack by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val selectedIndex = navItems.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        NavHost(
            navController = bottomNavController,
            startDestination = Route.HomeScreen.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 74.dp)
        ) {
            composable(Route.HomeScreen.route) { 
                HomeScreen(
                    navController = rootNavController,
                    onNavigateToTab = { route ->
                        bottomNavController.navigate(route) {
                            popUpTo(bottomNavController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Route.PrayerTimes.route) { }
            composable(Route.AzkarScreen.route) { }
            composable(Route.RadioScreen.route) { }
            composable(Route.SettingsScreen.route) { SettingsScreen(rootNavController) }
        }

        BottomNavigationBar(
            items = bottomItems,
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                val route = navItems[index].route
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
    }
}
