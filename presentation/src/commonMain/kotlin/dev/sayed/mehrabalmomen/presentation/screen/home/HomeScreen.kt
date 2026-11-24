package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.home.component.ContinueToTilawah
import dev.sayed.mehrabalmomen.presentation.screen.home.component.FeaturesSection
import dev.sayed.mehrabalmomen.presentation.screen.home.component.HomeAppBar
import dev.sayed.mehrabalmomen.presentation.screen.home.component.PrayerTimesCard
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.sayed.mehrabalmomen.presentation.components.AlarmPermissionsBottomSheet

@Composable
fun HomeScreen(
    navController: NavController,
    onNavigateToTab: ((String) -> Unit)? = null,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val language = LocalAppLocale.current
    var currentEffect by remember { mutableStateOf<HomeEffect?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getHijriDate(language)
    }

    AlarmPermissionHandler(
        effect = currentEffect,
        onPermissionsUpdated = viewModel::onScreenOpened
    )

    CollectEffect(viewModel.effect) { effect ->
        currentEffect = effect
        when (effect) {
            HomeEffect.NavigateToPrayerTimes -> {
                onNavigateToTab?.invoke(Route.PrayerTimes.route)
            }

            HomeEffect.NavigateToCalibrateDevice -> {
                navController.navigate(Route.CalibrateDevice)
            }

            HomeEffect.NavigateToSettings -> {
                onNavigateToTab?.invoke(Route.SettingsScreen.route)
            }

            HomeEffect.NavigateToQuran -> {
                navController.navigate(Route.SurahListScreen)
            }

            HomeEffect.NavigateToTilawah -> {
                navController.navigate(
                    Route.SurahAyatScreen(
                        surahId = state.lastTilawahUi.surahId,
                        arabicName = state.lastTilawahUi.nameArabic,
                        englishName = state.lastTilawahUi.nameEnglish,
                        targetAyahId = state.lastTilawahUi.ayahId
                    )
                )
            }

            HomeEffect.NavigateToLocation -> {
                navController.navigate(Route.MapsScreen)
            }

            HomeEffect.RequestAlarmPermissions -> {
                // Handled via AlarmPermissionHandler
            }

            else -> {}
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        item {
            HomeAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                state = state,
                onNotificationClick = {
                    navController.navigate(Route.NotificationsScreen)
                },
                onLocationClick = viewModel::onClickLocation
            )
        }
        item {
            PrayerTimesCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                prayerTimesUiState = state,
            )
        }
        if (state.hijriDate.isNotBlank()) {
            item {
                Text(
                    text = state.hijriDate,
                    color = Theme.color.secondary.shadeSecondary,
                    style = Theme.textStyle.label.small,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp)
                )
            }
        }
        item {
            ContinueToTilawah(
                onClick = {
                    navController.navigate(
                        Route.SurahAyatScreen(
                            surahId = state.lastTilawahUi.surahId,
                            arabicName = state.lastTilawahUi.nameArabic,
                            englishName = state.lastTilawahUi.nameEnglish,
                            targetAyahId = state.lastTilawahUi.ayahId
                        )
                    )
                },
                surahUiState = state.lastTilawahUi,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
        item { FeaturesSection(homeInteractionListener = viewModel) }
    }

    if (state.showAlarmPermissionBottomSheet) {
        AlarmPermissionsBottomSheet(
            isNotificationPermissionMissing = state.isNotificationPermissionMissing,
            isExactAlarmPermissionMissing = state.isExactAlarmPermissionMissing,
            isBatteryOptimizationMissing = state.isBatteryOptimizationMissing,
            onGrantNotification = viewModel::onGrantNotificationPermission,
            onGrantExactAlarm = viewModel::onGrantExactAlarmPermission,
            onGrantBattery = viewModel::onGrantBatteryPermission,
            onGrantAll = viewModel::onGrantAlarmPermissions,
            onIgnore = viewModel::onIgnoreAlarmPermissions
        )
    }
}
