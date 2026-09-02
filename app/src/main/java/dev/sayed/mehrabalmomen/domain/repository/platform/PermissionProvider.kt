package dev.sayed.mehrabalmomen.domain.repository.platform

/**
 * Interface for checking the status of platform-specific permissions.
 * Allows ViewModels to make decisions based on permissions without knowing Android APIs.
 */
interface PermissionProvider {

    /** Checks if the app has permission to access precise location. */
    fun hasLocationPermission(): Boolean

    /** Checks if the app has permission to post notifications. */
    fun hasNotificationPermission(): Boolean

    /** Checks if the app has permission to schedule exact alarms. */
    fun canScheduleExactAlarms(): Boolean

    /** Checks if the app is ignoring battery optimizations (Background permission). */
    fun isIgnoringBatteryOptimizations(): Boolean

    /** Returns true if notification permission is required for the current platform. */
    fun isNotificationPermissionRequired(): Boolean

    /** Returns true if exact alarm permission is required for the current platform. */
    fun isExactAlarmPermissionRequired(): Boolean
}
