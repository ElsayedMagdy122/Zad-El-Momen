package dev.sayed.mehrabalmomen.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Platform hook to request notification permission only when needed (matches UNS-App pattern).
 * - On Android 13+ (API 33+), checks POST_NOTIFICATIONS permission and requests it if missing before running [onGranted].
 * - On older Android versions where notification permission is granted by default at install time, executes [onGranted] immediately.
 */
@Composable
fun rememberNotificationPermissionLauncher(
    onGranted: () -> Unit = {},
    onDenied: (() -> Unit)? = null
): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            onDenied?.invoke() ?: onGranted()
        }
    }

    return remember(context, launcher, onGranted, onDenied) {
        {
            if (isNotificationPermissionRequired()) {
                val permission = Manifest.permission.POST_NOTIFICATIONS
                if (isNotificationPermissionGranted(context)) {
                    onGranted()
                } else {
                    launcher.launch(permission)
                }
            } else {
                onGranted()
            }
        }
    }
}

/**
 * Returns true if notification permission is granted on the current device.
 * For Android versions below 13 (TIRAMISU), this returns true by default.
 */
fun isNotificationPermissionGranted(context: Context): Boolean {
    return if (isNotificationPermissionRequired()) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

/**
 * Returns true if the device requires runtime permission for notifications (Android 13+ / API 33+).
 */
fun isNotificationPermissionRequired(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}

/**
 * Lifecycle-aware composable state for notification permission.
 * Automatically updates when the user returns to the app from system settings.
 */
@Composable
fun rememberNotificationPermissionState(): State<Boolean> {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(isNotificationPermissionGranted(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                state.value = isNotificationPermissionGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return state
}
