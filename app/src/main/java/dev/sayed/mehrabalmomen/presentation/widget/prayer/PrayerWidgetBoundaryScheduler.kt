package dev.sayed.mehrabalmomen.presentation.widget.prayer

import dev.sayed.mehrabalmomen.domain.repository.widget.ExactAlarmPermissionRepository

/**
 * Validates and coordinates exact widget alarms shared by all widget instances.
 *
 * @property exactAlarmPermissionRepository source of current exact-alarm access.
 * @property boundaryAlarm platform alarm implementation used after validation succeeds.
 * @property currentTimeMillis wall-clock source used to reject stale or non-future targets.
 */
class PrayerWidgetBoundaryScheduler(
    private val exactAlarmPermissionRepository: ExactAlarmPermissionRepository,
    private val boundaryAlarm: PrayerWidgetBoundaryAlarm,
    private val currentTimeMillis: () -> Long = { System.currentTimeMillis() },
) {
    /**
     * Schedules the next prayer boundary and next local-midnight refresh.
     *
     * Invalid targets and missing permission cancel any stale alarm so an obsolete countdown cannot
     * continue past zero. Repeated valid calls replace the same platform alarms rather than adding
     * alarms per widget instance.
     *
     * @param nextPrayerTargetEpochMillis absolute epoch-millis next-prayer instant, if available.
     * @param nextLocalMidnightEpochMillis absolute epoch-millis next local midnight, if available.
     * @return `true` only when both future widget alarms were accepted by the platform.
     */
    fun schedule(
        nextPrayerTargetEpochMillis: Long?,
        nextLocalMidnightEpochMillis: Long?,
    ): Boolean {
        val now = currentTimeMillis()
        val canSchedule = nextPrayerTargetEpochMillis != null &&
            nextPrayerTargetEpochMillis > now &&
            nextLocalMidnightEpochMillis != null &&
            nextLocalMidnightEpochMillis > now &&
            exactAlarmPermissionRepository.canScheduleExactAlarms()
        if (!canSchedule) {
            boundaryAlarm.cancelAll()
            return false
        }

        if (!boundaryAlarm.schedulePrayerBoundary(nextPrayerTargetEpochMillis)) {
            boundaryAlarm.cancelAll()
            return false
        }
        if (!boundaryAlarm.scheduleLocalMidnight(nextLocalMidnightEpochMillis)) {
            boundaryAlarm.cancelAll()
            return false
        }
        return true
    }

    /**
     * Cancels widget-only exact alarms without affecting Azan notification alarms.
     *
     * @return no value; after completion stale prayer-boundary and midnight alarms are removed.
     */
    fun cancelAll() {
        boundaryAlarm.cancelAll()
    }
}
