package dev.sayed.mehrabalmomen.presentation.widget.prayer

import dev.sayed.mehrabalmomen.domain.repository.widget.ExactAlarmPermissionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayerWidgetBoundarySchedulerTest {
    /**
     * Verifies that valid future targets schedule both shared widget alarm identities.
     *
     * @return no value; assertions fail if prayer-boundary or midnight targets are missing.
     */
    @Test
    fun `future targets with permission replace shared widget alarms`() {
        val alarm = RecordingBoundaryAlarm()
        val scheduler = scheduler(alarm = alarm)

        assertTrue(scheduler.schedule(2_000L, 2_500L))
        assertTrue(scheduler.schedule(3_000L, 3_500L))

        assertEquals(listOf(2_000L, 3_000L), alarm.prayerBoundaryTargets)
        assertEquals(listOf(2_500L, 3_500L), alarm.localMidnightTargets)
        assertEquals(0, alarm.cancellations)
    }

    @Test
    fun `missing exact alarm permission cancels stale boundary`() {
        val alarm = RecordingBoundaryAlarm()
        val scheduler = scheduler(alarm = alarm, canSchedule = false)

        assertFalse(scheduler.schedule(2_000L, 2_500L))

        assertEquals(1, alarm.cancellations)
        assertTrue(alarm.prayerBoundaryTargets.isEmpty())
        assertTrue(alarm.localMidnightTargets.isEmpty())
    }

    @Test
    fun `missing and stale targets cancel instead of scheduling`() {
        val alarm = RecordingBoundaryAlarm()
        val scheduler = scheduler(alarm = alarm)

        assertFalse(scheduler.schedule(null, 2_000L))
        assertFalse(scheduler.schedule(2_000L, null))
        assertFalse(scheduler.schedule(1_000L, 2_000L))
        assertFalse(scheduler.schedule(2_000L, 999L))

        assertEquals(4, alarm.cancellations)
        assertTrue(alarm.prayerBoundaryTargets.isEmpty())
        assertTrue(alarm.localMidnightTargets.isEmpty())
    }

    /**
     * Verifies that a platform refusal removes any partially scheduled widget alarms.
     *
     * @return no value; assertions fail if stale exact alarms remain after failure.
     */
    @Test
    fun `platform schedule failure cancels both widget alarms`() {
        val alarm = RecordingBoundaryAlarm(acceptLocalMidnight = false)
        val scheduler = scheduler(alarm = alarm)

        assertFalse(scheduler.schedule(2_000L, 2_500L))

        assertEquals(listOf(2_000L), alarm.prayerBoundaryTargets)
        assertTrue(alarm.localMidnightTargets.isEmpty())
        assertEquals(1, alarm.cancellations)
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

    private class RecordingBoundaryAlarm(
        private val acceptPrayerBoundary: Boolean = true,
        private val acceptLocalMidnight: Boolean = true,
    ) : PrayerWidgetBoundaryAlarm {
        val prayerBoundaryTargets = mutableListOf<Long>()
        val localMidnightTargets = mutableListOf<Long>()
        var cancellations = 0

        /**
         * Records an accepted boundary target.
         *
         * @param targetEpochMillis absolute target passed by the scheduler.
         * @return configured platform result for the prayer-boundary alarm.
         */
        override fun schedulePrayerBoundary(targetEpochMillis: Long): Boolean {
            if (acceptPrayerBoundary) {
                prayerBoundaryTargets += targetEpochMillis
            }
            return acceptPrayerBoundary
        }

        /**
         * Records an accepted local-midnight target.
         *
         * @param targetEpochMillis absolute target passed by the scheduler.
         * @return configured platform result for the local-midnight alarm.
         */
        override fun scheduleLocalMidnight(targetEpochMillis: Long): Boolean {
            if (acceptLocalMidnight) {
                localMidnightTargets += targetEpochMillis
            }
            return acceptLocalMidnight
        }

        /** Records cancellation of all shared widget alarms. */
        override fun cancelAll() {
            cancellations += 1
        }
    }
}
