package dev.sayed.mehrabalmomen.domain.repository.widget

/** Provides platform-independent access to the exact alarm permission state. */
interface ExactAlarmPermissionRepository {
    /**
     * Checks whether the application may schedule exact alarms on the current device.
     *
     * @return `true` when exact alarms are supported without permission or access is granted.
     */
    fun canScheduleExactAlarms(): Boolean
}
