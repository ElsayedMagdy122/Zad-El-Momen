package dev.sayed.mehrabalmomen.data.platform.system

import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse

class IosPermissionProvider : PermissionProvider {

    override fun hasLocationPermission(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedAlways ||
                status == kCLAuthorizationStatusAuthorizedWhenInUse
    }

    override fun hasNotificationPermission(): Boolean {
        return true
    }

    override fun canScheduleExactAlarms(): Boolean = true

    override fun isIgnoringBatteryOptimizations(): Boolean = true

    override fun canDrawOverlays(): Boolean = false

    override fun isNotificationPermissionRequired(): Boolean = true

    override fun isExactAlarmPermissionRequired(): Boolean = false
}
