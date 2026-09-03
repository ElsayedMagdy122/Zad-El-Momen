package dev.sayed.mehrabalmomen.presentation.utils

import androidx.compose.runtime.Composable

/**
 * Platform-independent interface for requesting system permissions.
 */
interface PermissionManager {
    fun requestLocationPermission(onResult: (Boolean) -> Unit)
    fun requestNotificationPermission(onResult: (Boolean) -> Unit)
    fun requestExactAlarmPermission()
    fun requestIgnoreBatteryOptimization()
}

@Composable
expect fun rememberPermissionManager(): PermissionManager
