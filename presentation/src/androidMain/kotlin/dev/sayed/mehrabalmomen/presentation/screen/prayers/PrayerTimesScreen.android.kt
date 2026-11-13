package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.sayed.mehrabalmomen.presentation.utils.AutoPermissionRequester
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import dev.sayed.mehrabalmomen.presentation.utils.openAppSettings
import dev.sayed.mehrabalmomen.presentation.utils.openOverlaySettings
import dev.sayed.mehrabalmomen.presentation.utils.rememberBatteryOptimizationLauncher
import dev.sayed.mehrabalmomen.presentation.utils.rememberExactAlarmPermissionLauncher
import dev.sayed.mehrabalmomen.presentation.utils.rememberNotificationPermissionLauncher

@Composable
actual fun HandlePrayerTimesPlatformEffects(
    viewModel: PrayerTimesViewModel
) {
    val context = LocalContext.current
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

            PrayerTimesEffect.RequestOverlayPermission -> {
                openOverlaySettings(context)
            }

            PrayerTimesEffect.OpenAppSettings -> {
                openAppSettings(context)
            }

            else -> {}
        }
    }
}
