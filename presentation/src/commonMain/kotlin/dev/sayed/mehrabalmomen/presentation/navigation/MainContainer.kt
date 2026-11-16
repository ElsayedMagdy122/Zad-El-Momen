package dev.sayed.mehrabalmomen.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.model.DonationPromptAction
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.azkar.AzkarScreen
import dev.sayed.mehrabalmomen.presentation.screen.companion.CompanionOverlay
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeScreen
import dev.sayed.mehrabalmomen.presentation.screen.prayers.PrayerTimesScreen
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioScreen
import dev.sayed.mehrabalmomen.presentation.screen.settings.SelectionItem
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsBottomSheet
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
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
import zadelmomen.presentation.generated.resources.support_1
import zadelmomen.presentation.generated.resources.support_2
import zadelmomen.presentation.generated.resources.support_3
import zadelmomen.presentation.generated.resources.support_4
import zadelmomen.presentation.generated.resources.support_5
import zadelmomen.presentation.generated.resources.support_description
import zadelmomen.presentation.generated.resources.support_developer

private const val SUPPORT_1 = "support_1"
private const val SUPPORT_5 = "support_5"
private const val SUPPORT_10 = "support_10"
private const val SUPPORT_25 = "support_25"
private const val SUPPORT_100 = "support_100"

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun MainContainer(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    val settingsRepository: SettingsRepository = koinInject()
    val billingService: BillingService = koinInject()
    val scope = rememberCoroutineScope()

    var showAutoSupportDialog by remember { mutableStateOf(false) }
    val products by billingService.products.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        val randomDelaySeconds = (10..15).random()
        delay(randomDelaySeconds * 1000L)
        val currentTime = Clock.System.now().toEpochMilliseconds()
        if (settingsRepository.shouldShowDonationPrompt(currentTime)) {
            showAutoSupportDialog = true
        }
    }

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
            composable(Route.HomeScreen.route) { HomeScreen(rootNavController) }
            composable(Route.PrayerTimes.route) { PrayerTimesScreen(rootNavController) }
            composable(Route.AzkarScreen.route) { AzkarScreen(rootNavController) }
            composable(Route.RadioScreen.route) { RadioScreen(rootNavController) }
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

        CompanionOverlay()

        if (showAutoSupportDialog) {
            val supportProductIds = listOf(
                SUPPORT_1,
                SUPPORT_5,
                SUPPORT_10,
                SUPPORT_25,
                SUPPORT_100
            )
            val options = supportProductIds.map { id ->
                val donationProduct = products.find { it.id == id }
                val price = donationProduct?.price ?: "N/A"
                SelectionItem(
                    description = price,
                    text = UiText.StringRes(getSupportName(id))
                )
            }
            SettingsBottomSheet(
                items = options,
                title = UiText.StringRes(Res.string.support_developer),
                description = UiText.StringRes(Res.string.support_description),
                selectedIndex = -1,
                onConfirm = { index ->
                    scope.launch {
                        settingsRepository.onDonationPromptAction(DonationPromptAction.DONATED, 0L)
                    }
                    showAutoSupportDialog = false
                },
                onDismiss = {
                    scope.launch {
                        settingsRepository.onDonationPromptAction(DonationPromptAction.DISMISSED, 0L)
                    }
                    showAutoSupportDialog = false
                }
            )
        }
    }
}

private fun getSupportName(id: String) = when (id) {
    SUPPORT_1 -> Res.string.support_1
    SUPPORT_5 -> Res.string.support_2
    SUPPORT_10 -> Res.string.support_3
    SUPPORT_25 -> Res.string.support_4
    SUPPORT_100 -> Res.string.support_5
    else -> Res.string.support_developer
}
