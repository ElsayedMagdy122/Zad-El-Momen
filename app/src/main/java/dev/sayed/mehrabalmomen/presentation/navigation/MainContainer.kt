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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.BottomNavigationBar
import dev.sayed.mehrabalmomen.design_system.component.NavItem
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeScreen
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesViewScreen
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioScreen
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.sayed.mehrabalmomen.presentation.screen.home_reels.HomeReelsScreen

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun MainContainer(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    val bottomNavController = rememberNavController()

    val isBottomBarVisible = remember { mutableStateOf(true) }
    val navItems = listOf(
        Route.HomeScreen,
        Route.FullPrayerTimeView,
        Route.HomeReelsScreen,
        Route.RadioScreen,
        Route.SettingsScreen,
    )

    val bottomItems = listOf(
        NavItem(
            title = localizedString(R.string.home),
            selectedIcon = painterResource(R.drawable.ic_home_selected),
            unselectedIcon = painterResource(R.drawable.ic_home_not_selected)
        ),
        NavItem(
            title = localizedString(R.string.prayer),
            selectedIcon = painterResource(R.drawable.ic_prayer_times_selected),
            unselectedIcon = painterResource(R.drawable.ic_prayer_times_not_selected)
        ),
        NavItem(
            title = localizedString(R.string.reels),
            selectedIcon = painterResource(R.drawable.ic_reels_selected),
            unselectedIcon = painterResource(R.drawable.ic_reels_not_selected)
        ),
        NavItem(
            title = localizedString(R.string.radio),
            selectedIcon = painterResource(R.drawable.ic_radio_selected),
            unselectedIcon = painterResource(R.drawable.ic_radio_not_selected)
        ),
        NavItem(
            title = localizedString(R.string.settings),
            selectedIcon = painterResource(R.drawable.ic_settings_selected),
            unselectedIcon = painterResource(R.drawable.ic_settings_not_selected)
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
        val bottomPadding by animateDpAsState(
            targetValue = if (isBottomBarVisible.value) 74.dp else 0.dp,
            label = "bottom_padding"
        )
        NavHost(
            navController = bottomNavController,
            startDestination = Route.HomeScreen.route,
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.color.surfaces.surface)
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(bottom = bottomPadding)
        ) {
            composable(Route.HomeScreen.route) { HomeScreen(rootNavController) }
            composable(Route.FullPrayerTimeView.route) { FullPrayerTimesViewScreen(rootNavController) }

            composable(Route.RadioScreen.route) { RadioScreen(rootNavController) }
            composable(Route.SettingsScreen.route) { SettingsScreen(rootNavController) }
            composable(Route.HomeReelsScreen.route) { HomeReelsScreen(navController = rootNavController) }
        }

        AnimatedVisibility(
            visible = isBottomBarVisible.value,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {

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
                }
            )
        }
    }
}