package dev.sayed.mehrabalmomen.presentation.reciver

import android.app.AlarmManager
import android.content.Intent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BootReceiverTest {
    /**
     * Verifies that every manifest lifecycle action used by Stage 5 refreshes the widget.
     *
     * @return no value; assertions fail if a supported lifecycle action is ignored.
     */
    @Test
    fun `manifest lifecycle actions refresh the prayer widget`() {
        val refreshActions = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_LOCALE_CHANGED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
        )

        refreshActions.forEach { action ->
            assertTrue(action.shouldRefreshPrayerWidget())
        }
        assertFalse(Intent.ACTION_DATE_CHANGED.shouldRefreshPrayerWidget())
        assertFalse((null as String?).shouldRefreshPrayerWidget())
    }

    /**
     * Verifies that only device boot recreates non-widget Azan and reminder alarms.
     *
     * @return no value; assertions fail if time or permission changes reschedule Azan alarms.
     */
    @Test
    fun `only boot recreates Azan and reminder alarms`() {
        assertTrue(Intent.ACTION_BOOT_COMPLETED.shouldReschedulePrayerAlarms())
        assertFalse(Intent.ACTION_TIME_CHANGED.shouldReschedulePrayerAlarms())
        assertFalse(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.shouldReschedulePrayerAlarms())
    }
}
