package dev.sayed.mehrabalmomen.presentation.utils

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Automatically checks and prompts system permission dialogs directly to the user
 * when entering a screen (such as Prayer times or Reminders) without showing any custom UI/Design dialogs.
 *
 * Sequence of system requests:
 * 1. POST_NOTIFICATIONS (Native OS dialog on Android 13+)
 * 2. SCHEDULE_EXACT_ALARM (Native system settings page on Android 12+)
 * 3. REQUEST_IGNORE_BATTERY_OPTIMIZATIONS (Native system white-list dialog on Android 6+)
 */
@Composable
fun AutoPermissionRequester() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Track whether we already triggered each step in this session to avoid infinite loops on resume
    var hasRequestedNotification by rememberSaveable { mutableStateOf(false) }
    var hasRequestedExactAlarm by rememberSaveable { mutableStateOf(false) }
    var hasRequestedBattery by rememberSaveable { mutableStateOf(false) }

    val requestBatteryOptimization = rememberBatteryOptimizationLauncher()
    val requestExactAlarm = rememberExactAlarmPermissionLauncher()

    val requestNotification = rememberNotificationPermissionLauncher(
        onGranted = {
            // Check next permission immediately
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactAlarms(context) && !hasRequestedExactAlarm) {
                hasRequestedExactAlarm = true
                requestExactAlarm()
            } else if (isBatteryOptimizationSupported() && !isIgnoringBatteryOptimizations(context) && !hasRequestedBattery) {
                hasRequestedBattery = true
                requestBatteryOptimization()
            }
        },
        onDenied = {
            // If user denies notifications, still check remaining permissions if not done yet
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactAlarms(context) && !hasRequestedExactAlarm) {
                hasRequestedExactAlarm = true
                requestExactAlarm()
            } else if (isBatteryOptimizationSupported() && !isIgnoringBatteryOptimizations(context) && !hasRequestedBattery) {
                hasRequestedBattery = true
                requestBatteryOptimization()
            }
        }
    )

    fun checkAndRequestNextPermission() {
        if (isNotificationPermissionRequired() && !isNotificationPermissionGranted(context)) {
            if (!hasRequestedNotification) {
                hasRequestedNotification = true
                requestNotification()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactAlarms(context)) {
            if (!hasRequestedExactAlarm) {
                hasRequestedExactAlarm = true
                requestExactAlarm()
            }
        } else if (isBatteryOptimizationSupported() && !isIgnoringBatteryOptimizations(context)) {
            if (!hasRequestedBattery) {
                hasRequestedBattery = true
                requestBatteryOptimization()
            }
        }
    }

    LaunchedEffect(Unit) {
        checkAndRequestNextPermission()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkAndRequestNextPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
