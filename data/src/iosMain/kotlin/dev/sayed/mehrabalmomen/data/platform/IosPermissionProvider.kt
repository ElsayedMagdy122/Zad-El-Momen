package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider

class IosPermissionProvider : PermissionProvider {
    override fun hasLocationPermission(): Boolean = true
    override fun hasNotificationPermission(): Boolean = true
    override fun canScheduleExactAlarms(): Boolean = true
    override fun isIgnoringBatteryOptimizations(): Boolean = true
    override fun canDrawOverlays(): Boolean = true
    override fun isNotificationPermissionRequired(): Boolean = false
    override fun isExactAlarmPermissionRequired(): Boolean = false
}
