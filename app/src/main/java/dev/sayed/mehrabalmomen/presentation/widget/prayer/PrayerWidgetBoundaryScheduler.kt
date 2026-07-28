package dev.sayed.mehrabalmomen.presentation.widget.prayer

import dev.sayed.mehrabalmomen.domain.repository.widget.ExactAlarmPermissionRepository

/**
 * Validates and coordinates the one exact prayer-boundary alarm shared by all widget instances.
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
     * Schedules the next prayer boundary only when the target is future and access is available.
     *
     * Invalid targets and missing permission cancel any stale alarm so an obsolete countdown cannot
     * continue past zero. Repeated valid calls replace the same platform alarm rather than adding
     * one alarm per widget instance.
     *
     * @param targetEpochMillis absolute epoch-millis next-prayer instant, if available.
     * @return `true` only when a future boundary alarm was accepted by the platform.
     */
    fun schedule(targetEpochMillis: Long?): Boolean {
        val canSchedule = targetEpochMillis != null &&
            targetEpochMillis > currentTimeMillis() &&
            exactAlarmPermissionRepository.canScheduleExactAlarms()
        if (!canSchedule) {
            boundaryAlarm.cancel()
            return false
        }

        return boundaryAlarm.schedule(requireNotNull(targetEpochMillis))
    }

    /** Cancels the widget-only prayer-boundary alarm without affecting Azan notification alarms. */
    fun cancel() {
        boundaryAlarm.cancel()
    }
}
