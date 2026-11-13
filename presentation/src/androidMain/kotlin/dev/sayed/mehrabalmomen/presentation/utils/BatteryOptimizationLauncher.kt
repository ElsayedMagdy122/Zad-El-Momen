package dev.sayed.mehrabalmomen.presentation.utils

import android.app.ActivityManager
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Platform hook to request ignoring battery optimizations (background execution permission),
 * matching the pattern used in UNS-App.
 *
 * If the app is already exempt from battery optimization, it invokes [onAlreadyIgnored] or does nothing.
 * Otherwise, it directly prompts the system dialog (ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
 * or falls back to system battery / app settings.
 */
@Composable
fun rememberBatteryOptimizationLauncher(
    onAlreadyIgnored: (() -> Unit)? = null
): () -> Unit {
    val context = LocalContext.current

    return remember(context, onAlreadyIgnored) {
        {
            requestIgnoreBatteryOptimizations(
                context = context,
                onAlreadyIgnored = onAlreadyIgnored
            )
        }
    }
}

/**
 * Checks whether the app is currently whitelisted / exempt from battery optimizations.
 * When true, the OS does not restrict background tasks, alarms, or wake locks.
 */
fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: true
    } else {
        true
    }
}

/**
 * Checks whether the user has placed background restrictions on this app (Android 9+ / API 28+).
 */
fun isBackgroundRestricted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        activityManager?.isBackgroundRestricted ?: false
    } else {
        false
    }
}

/**
 * Checks if the system supports and requires battery optimization exemption.
 */
fun isBatteryOptimizationSupported(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

/**
 * Requests the system to ignore battery optimizations for this app.
 * Requires android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS in AndroidManifest.xml.
 */
fun requestIgnoreBatteryOptimizations(
    context: Context,
    onAlreadyIgnored: (() -> Unit)? = null
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val packageName = context.packageName

        if (powerManager?.isIgnoringBatteryOptimizations(packageName) == true) {
            if (onAlreadyIgnored != null) {
                onAlreadyIgnored.invoke()
            } else {
                openAppSettings(context)
            }
            return
        }

        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
    } else {
        onAlreadyIgnored?.invoke()
    }
}

/**
 * Lifecycle-aware composable state for battery optimization exemption.
 * Automatically refreshes when user navigates back to the app from settings.
 */
@Composable
fun rememberBatteryOptimizationState(): State<Boolean> {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(isIgnoringBatteryOptimizations(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                state.value = isIgnoringBatteryOptimizations(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return state
}

/**
 * Hook to request exact alarm permission (Android 12+ / API 31+).
 */
@Composable
fun rememberExactAlarmPermissionLauncher(
    onAlreadyGranted: (() -> Unit)? = null
): () -> Unit {
    val context = LocalContext.current

    return remember(context, onAlreadyGranted) {
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                if (alarmManager?.canScheduleExactAlarms() == true) {
                    onAlreadyGranted?.invoke()
                } else {
                    try {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.parse("package:${context.packageName}")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                }
            } else {
                onAlreadyGranted?.invoke()
            }
        }
    }
}

/**
 * Checks whether the app can schedule exact alarms (essential for Azan / reminders).
 */
fun canScheduleExactAlarms(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.canScheduleExactAlarms() ?: true
    } else {
        true
    }
}

/**
 * Checks whether the app has permission to draw overlays (Display over other apps).
 */
fun canDrawOverlays(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.canDrawOverlays(context)
    } else {
        true
    }
}

/**
 * Opens the system settings screen for "Display over other apps" for this app.
 */
fun openOverlaySettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                openAppSettings(context)
            }
        }
    } else {
        openAppSettings(context)
    }
}

/**
 * Opens application details settings in Android system settings.
 */
fun openAppSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
