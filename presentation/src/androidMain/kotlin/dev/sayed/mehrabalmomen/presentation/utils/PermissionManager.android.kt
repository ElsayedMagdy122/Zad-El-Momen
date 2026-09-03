package dev.sayed.mehrabalmomen.presentation.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidPermissionManager(
    private val context: Context,
    private val locationLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    private val notificationLauncher: androidx.activity.result.ActivityResultLauncher<String>
) : PermissionManager {

    override fun requestLocationPermission(onResult: (Boolean) -> Unit) {
        locationLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun requestNotificationPermission(onResult: (Boolean) -> Unit) {
        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun requestExactAlarmPermission() {
        val intent = Intent().apply {
            action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override fun requestIgnoreBatteryOptimization() {
        val intent = Intent().apply {
            action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

@Composable
actual fun rememberPermissionManager(): PermissionManager {
    // Note: We need a way to pass results back to the caller.
    // This is a simplified implementation. 
    // In a real app, you might want to handle the results more robustly.
    
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val context = LocalContext.current
    return remember(context) {
        AndroidPermissionManager(context, locationLauncher, notificationLauncher)
    }
}
