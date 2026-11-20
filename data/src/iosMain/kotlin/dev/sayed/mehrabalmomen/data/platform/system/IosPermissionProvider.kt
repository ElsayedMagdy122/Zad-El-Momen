package dev.sayed.mehrabalmomen.data.platform.system

import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.Foundation.NSUserDefaults

class IosPermissionProvider : PermissionProvider {

    override fun hasLocationPermission(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedAlways ||
                status == kCLAuthorizationStatusAuthorizedWhenInUse
    }

    override fun hasNotificationPermission(): Boolean {
        return NSUserDefaults.standardUserDefaults.boolForKey("ios_notification_permission_granted")
    }

    override fun canScheduleExactAlarms(): Boolean = true

    override fun isIgnoringBatteryOptimizations(): Boolean = true

    override fun canDrawOverlays(): Boolean = false

    override fun isNotificationPermissionRequired(): Boolean = true

    override fun isExactAlarmPermissionRequired(): Boolean = false
}
