package dev.sayed.mehrabalmomen.domain.repository.platform

/**
 * Interface for checking the status of platform-specific permissions.
 */
interface PermissionProvider {
    fun hasLocationPermission(): Boolean
    fun hasNotificationPermission(): Boolean
    fun canScheduleExactAlarms(): Boolean
    fun isIgnoringBatteryOptimizations(): Boolean
    fun isNotificationPermissionRequired(): Boolean
    fun isExactAlarmPermissionRequired(): Boolean
}
