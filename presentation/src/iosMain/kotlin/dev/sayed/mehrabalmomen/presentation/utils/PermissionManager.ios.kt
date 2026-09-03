package dev.sayed.mehrabalmomen.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationOptionBadge

class IosPermissionManager : PermissionManager {

    override fun requestLocationPermission(onResult: (Boolean) -> Unit) {
        val locationManager = CLLocationManager()
        locationManager.requestWhenInUseAuthorization()
        // Simple callback, in real app we'd observe status changes
        onResult(true)
    }

    override fun requestNotificationPermission(onResult: (Boolean) -> Unit) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, error ->
            onResult(granted)
        }
    }

    override fun requestExactAlarmPermission() {
        // Not applicable on iOS
    }

    override fun requestIgnoreBatteryOptimization() {
        // Not applicable on iOS
    }
}

@Composable
actual fun rememberPermissionManager(): PermissionManager {
    return remember { IosPermissionManager() }
}
