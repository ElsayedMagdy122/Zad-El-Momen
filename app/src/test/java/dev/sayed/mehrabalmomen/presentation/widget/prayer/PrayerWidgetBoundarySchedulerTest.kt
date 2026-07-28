package dev.sayed.mehrabalmomen.presentation.widget.prayer

import dev.sayed.mehrabalmomen.domain.repository.widget.ExactAlarmPermissionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayerWidgetBoundarySchedulerTest {
    @Test
    fun `future target with permission replaces the shared boundary alarm`() {
        val alarm = RecordingBoundaryAlarm()
        val scheduler = scheduler(alarm = alarm)

        assertTrue(scheduler.schedule(2_000L))
        assertTrue(scheduler.schedule(3_000L))

        assertEquals(listOf(2_000L, 3_000L), alarm.scheduledTargets)
        assertEquals(0, alarm.cancellations)
    }

    @Test
    fun `missing exact alarm permission cancels stale boundary`() {
        val alarm = RecordingBoundaryAlarm()
        val scheduler = scheduler(alarm = alarm, canSchedule = false)

        assertFalse(scheduler.schedule(2_000L))

        assertEquals(1, alarm.cancellations)
        assertTrue(alarm.scheduledTargets.isEmpty())
    }

    @Test
    fun `missing and stale targets cancel instead of scheduling`() {
        val alarm = RecordingBoundaryAlarm()
        val scheduler = scheduler(alarm = alarm)

        assertFalse(scheduler.schedule(null))
        assertFalse(scheduler.schedule(1_000L))
        assertFalse(scheduler.schedule(999L))

        assertEquals(3, alarm.cancellations)
        assertTrue(alarm.scheduledTargets.isEmpty())
    }

    /**
     * Creates a scheduler with deterministic permission, alarm, and wall-clock dependencies.
     *
     * @param alarm recording alarm boundary used to verify scheduling side effects.
     * @param canSchedule exact-alarm permission value returned to the scheduler.
     * @return scheduler whose current wall clock is fixed at epoch millisecond 1000.
     */
    private fun scheduler(
        alarm: RecordingBoundaryAlarm,
        canSchedule: Boolean = true,
    ): PrayerWidgetBoundaryScheduler {
        return PrayerWidgetBoundaryScheduler(
            exactAlarmPermissionRepository = FixedExactAlarmPermissionRepository(canSchedule),
            boundaryAlarm = alarm,
            currentTimeMillis = { 1_000L },
        )
    }

    private class FixedExactAlarmPermissionRepository(
        private val canSchedule: Boolean,
    ) : ExactAlarmPermissionRepository {
        /** @return the fixed exact-alarm permission configured by the test. */
        override fun canScheduleExactAlarms(): Boolean = canSchedule
    }

    private class RecordingBoundaryAlarm : PrayerWidgetBoundaryAlarm {
        val scheduledTargets = mutableListOf<Long>()
        var cancellations = 0

        /**
         * Records an accepted boundary target.
         *
         * @param targetEpochMillis absolute target passed by the scheduler.
         * @return always `true` to model a platform-accepted alarm.
         */
        override fun schedule(targetEpochMillis: Long): Boolean {
            scheduledTargets += targetEpochMillis
            return true
        }

        /** Records cancellation of the shared boundary alarm. */
        override fun cancel() {
            cancellations += 1
        }
    }
}
