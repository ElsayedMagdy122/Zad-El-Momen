package dev.sayed.mehrabalmomen.presentation.screen.prayers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.BatteryOptimizationDialog
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.NextPrayerCard
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.PrayerItem
import dev.sayed.mehrabalmomen.presentation.utils.AutoPermissionRequester
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import dev.sayed.mehrabalmomen.presentation.utils.rememberBatteryOptimizationLauncher
import dev.sayed.mehrabalmomen.presentation.utils.rememberExactAlarmPermissionLauncher
import dev.sayed.mehrabalmomen.presentation.utils.rememberNotificationPermissionLauncher
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime

@RequiresApi(33)
@SuppressLint("BatteryLife")
@ExperimentalTime
@Composable
fun PrayerTimesScreen(
    navController: NavController,
    viewModel: PrayerTimesViewModel = koinViewModel(),
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val countdownTime by viewModel.countdownTime.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshBatteryStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val notificationLauncher = rememberNotificationPermissionLauncher()
    val exactAlarmLauncher = rememberExactAlarmPermissionLauncher()
    val batteryOptimizationLauncher = rememberBatteryOptimizationLauncher()

    // Automatically prompt missing permissions directly with system dialogs on screen entry
    AutoPermissionRequester()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {

            PrayerTimesEffect.RequestExactAlarm -> {
                exactAlarmLauncher()
            }

            PrayerTimesEffect.RequestNotificationPermission -> {
                notificationLauncher()
            }

            PrayerTimesEffect.RequestIgnoreBatteryOptimization -> {
                batteryOptimizationLauncher()
            }

            PrayerTimesEffect.NavigateBack -> {
                navController.popBackStack()
            }

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
                isBackEnabled = false,
                onBackClick = viewModel::onClickBack,
                title = localizedString(R.string.prayer_times),
                modifier = Modifier.padding(
                    horizontal = 16.dp
                ),
                actionIcon = R.drawable.ic_warning,
                actionIconTint = Theme.color.primary.primary,
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

    if (state.showBatteryDialog) {
        BatteryOptimizationDialog(
            instructions = state.batteryInstructions,
            onDismiss = viewModel::onDismissBatteryDialog,
            listener = viewModel
        )
    }
}
