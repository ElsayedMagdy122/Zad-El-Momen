package dev.sayed.mehrabalmomen.presentation.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.sayed.mehrabalmomen.BuildConfig
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.util.BillingManager
import dev.sayed.mehrabalmomen.design_system.component.BottomNavigationBar
import dev.sayed.mehrabalmomen.design_system.component.NavItem
import dev.sayed.mehrabalmomen.design_system.theme.Theme
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
import dev.sayed.mehrabalmomen.presentation.screen.settings.getSupportName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun MainContainer(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    val settingsRepository: SettingsRepository = koinInject()
    val billingManager: BillingManager = koinInject()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val scope = rememberCoroutineScope()

    var showAutoSupportDialog by remember { mutableStateOf(false) }
    val products by billingManager.products.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        val randomDelaySeconds = (10..15).random()
        delay(randomDelaySeconds * 1000L)
        val currentTime = System.currentTimeMillis()
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
            title = localizedString(R.string.azkar),
            selectedIcon = painterResource(R.drawable.ic_azkar_selected),
            unselectedIcon = painterResource(R.drawable.ic_azkar_not_selected)
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
                BuildConfig.SUPPORT_1,
                BuildConfig.SUPPORT_5,
                BuildConfig.SUPPORT_10,
                BuildConfig.SUPPORT_25,
                BuildConfig.SUPPORT_100
            )
            val options = supportProductIds.map { id ->
                val donationProduct = products.find { it.id == id }
                val price = donationProduct?.price ?: "N/A"
                SelectionItem(
                    description = price,
                    text = UiText.StringResource(getSupportName(id))
                )
            }
            SettingsBottomSheet(
                settingsViewModel = null,
                isMoazen = false,
                items = options,
                title = UiText.StringResource(R.string.support_developer),
                description = UiText.StringResource(R.string.support_description),
                selectedIndex = -1,
                onConfirm = { index ->
                    val productId = when (index) {
                        0 -> BuildConfig.SUPPORT_1
                        1 -> BuildConfig.SUPPORT_5
                        2 -> BuildConfig.SUPPORT_10
                        3 -> BuildConfig.SUPPORT_25
                        4 -> BuildConfig.SUPPORT_100
                        else -> ""
                    }
                    activity?.let { billingManager.donate(it, productId) }
                    scope.launch {
                        settingsRepository.onDonationPromptAction(DonationPromptAction.DONATED, System.currentTimeMillis())
                    }
                    showAutoSupportDialog = false
                },
                onDismiss = {
                    scope.launch {
                        settingsRepository.onDonationPromptAction(DonationPromptAction.DISMISSED, System.currentTimeMillis())
                    }
                    showAutoSupportDialog = false
                }
            )
        }
    }
}

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return currentContext as? Activity
}
