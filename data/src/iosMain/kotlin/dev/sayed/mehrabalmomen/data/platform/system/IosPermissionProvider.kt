package dev.sayed.mehrabalmomen.data.platform.system

import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNAuthorizationStatusAuthorized

class IosPermissionProvider : PermissionProvider {
    
    override fun hasLocationPermission(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedAlways || 
               status == kCLAuthorizationStatusAuthorizedWhenInUse
    }

    override fun hasNotificationPermission(): Boolean {
        // This is async on iOS, for simple provider we might need a stored value or just assume false until requested
        return false 
    }

    override fun canScheduleExactAlarms(): Boolean = true // Not applicable, return true to skip

    override fun isIgnoringBatteryOptimizations(): Boolean = true // Not applicable, return true to skip

    override fun isNotificationPermissionRequired(): Boolean = true

    override fun isExactAlarmPermissionRequired(): Boolean = false
}
