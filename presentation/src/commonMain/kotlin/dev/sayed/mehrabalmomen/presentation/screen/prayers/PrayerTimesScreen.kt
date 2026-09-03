package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.NextPrayerCard
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.PrayerItem
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import zad_el_momen.presentation.generated.resources.Res
import zad_el_momen.presentation.generated.resources.*
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun PrayerTimesScreen(
    onNavigateBack: () -> Unit,
    onRequestExactAlarm: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestIgnoreBatteryOptimization: () -> Unit,
    viewModel: PrayerTimesViewModel = koinViewModel(),
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val countdownTime by viewModel.countdownTime.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            PrayerTimesEffect.RequestExactAlarm -> {
                onRequestExactAlarm()
            }
            PrayerTimesEffect.RequestNotificationPermission -> {
                onRequestNotificationPermission()
            }
            PrayerTimesEffect.RequestIgnoreBatteryOptimization -> {
                onRequestIgnoreBatteryOptimization()
            }
            PrayerTimesEffect.NavigateBack -> {
                onNavigateBack()
            }
            else -> {}
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.onScreenOpened()
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        item {
            AppBar(
                isBackEnabled = true,
                onBackClick = viewModel::onClickBack,
                title = stringResource(Res.string.prayer_times),
                modifier = Modifier.padding(horizontal = 16.dp),
                actionIcon = Res.drawable.ic_warning,
                onActionClick = viewModel::onBatteryWarningClick
            )
        }
        item {
            NextPrayerCard(
                state = state,
                countdownTime = countdownTime,
            )
        }
        items(state.prayers) { prayerUiState ->
            PrayerItem(
                prayerName = prayerUiState.prayerName,
                prayerLabel = prayerUiState.name,
                prayerTime = prayerUiState.time.time,
                isAm = prayerUiState.time.isAm,
                isNextPrayer = prayerUiState.isUpComing,
                isNotificationEnabled = prayerUiState.isNotificationEnabled,
                onNotificationClick = { name, enabled ->
                    viewModel.onClickEnablePrayer(name, enabled)
                }
            )
        }
    }
}
