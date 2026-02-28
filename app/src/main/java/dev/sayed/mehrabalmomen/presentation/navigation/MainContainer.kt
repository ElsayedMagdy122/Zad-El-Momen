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
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahScreen
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioScreen

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun MainContainer(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {

    val bottomNavController = rememberNavController()

    val navItems = listOf(
        Route.HomeScreen,
        Route.FullPrayerTimeView,
        Route.RadioScreen,
        Route.QiblahScreen,
        Route.QiblahScreen
    )

    val bottomItems = listOf(
        NavItem(
            title = localizedString(R.string.home),
            selectedIcon = painterResource(R.drawable.ic_prayer_rug_selected),
            unselectedIcon = painterResource(R.drawable.ic_prayer_rug_not_selected)
        ),
        NavItem(
            title = localizedString(R.string.prayer_times),
            selectedIcon = painterResource(R.drawable.ic_prayer_times_selected),
            unselectedIcon = painterResource(R.drawable.ic_prayer_times_not_selected)
        ),
        NavItem(
            title = "Radio",
            selectedIcon = painterResource(R.drawable.ic_radio_selected),
            unselectedIcon = painterResource(R.drawable.ic_radio_not_selected)
        ),
        NavItem(
            title = "Radio",
            selectedIcon = painterResource(R.drawable.ic_radio_selected),
            unselectedIcon = painterResource(R.drawable.ic_radio_not_selected)
        ),
        NavItem(
            title = "Radio",
            selectedIcon = painterResource(R.drawable.ic_radio_selected),
            unselectedIcon = painterResource(R.drawable.ic_radio_not_selected)
        )
    )

    val currentBackStack by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val selectedIndex = navItems.indexOfFirst {
        it::class.qualifiedName == currentRoute
    }.coerceAtLeast(0)

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
                HomeScreen(rootNavController)
            }

            composable<Route.FullPrayerTimeView> {
                FullPrayerTimesViewScreen(rootNavController)
            }

            composable<Route.RadioScreen> {
                RadioScreen(rootNavController)
            }
            composable<Route.QiblahScreen> {
                QiblahScreen(rootNavController)
            }
            composable<Route.QiblahScreen> {
                QiblahScreen(rootNavController)
            }
        }

        BottomNavigationBar(
            items = bottomItems,
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                bottomNavController.navigate(navItems[index]) {
                    popUpTo(navItems.first())
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}