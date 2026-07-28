package dev.sayed.mehrabalmomen.presentation.reciver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUpdateCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class BootReceiver : BroadcastReceiver() {
    /**
     * Routes supported system lifecycle broadcasts to Azan/reminder recovery and widget refresh.
     *
     * @param context receiver context supplied by Android for this broadcast.
     * @param intent delivered system broadcast, if Android supplied one.
     * @return no value; after completion, supported actions have started async recovery work.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (!action.shouldRefreshPrayerWidget()) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (action.shouldReschedulePrayerAlarms()) {
                    reschedulePrayerAlarmsAndReminders()
                }
                GlobalContext.get().get<PrayerWidgetUpdateCoordinator>().refreshAllIfInstalled()
            } finally {
                pendingResult.finish()
            }
        }
    }

    /**
     * Recreates non-widget prayer and reminder alarms after device boot.
     *
     * Widget scheduling stays outside this helper so disabled Azan notifications cannot disable
     * prayer-widget transitions.
     *
     * @return no value; after completion, Azan and reminder repositories have attempted recovery.
     */
    private suspend fun reschedulePrayerAlarmsAndReminders() {
        val koin = GlobalContext.get()
        runCatching {
            koin.get<PrayerSchedulingUseCase>().rescheduleTodayPrayerAlarms()
        }
        runCatching {
            koin.get<ReminderSchedulerRepository>().rescheduleAll()
        }
    }
}

/**
 * Checks whether this system broadcast should trigger a prayer-widget refresh.
 *
 * @receiver broadcast action string supplied by Android, if present.
 * @return `true` for widget lifecycle recovery actions, otherwise `false`.
 */
internal fun String?.shouldRefreshPrayerWidget(): Boolean {
    return this == Intent.ACTION_BOOT_COMPLETED ||
        this == Intent.ACTION_MY_PACKAGE_REPLACED ||
        this == Intent.ACTION_TIME_CHANGED ||
        this == Intent.ACTION_TIMEZONE_CHANGED ||
        this == Intent.ACTION_LOCALE_CHANGED ||
        this == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
}

/**
 * Checks whether this system broadcast should recreate Azan and reminder alarms.
 *
 * @receiver broadcast action string supplied by Android, if present.
 * @return `true` only for device boot, otherwise `false`.
 */
internal fun String?.shouldReschedulePrayerAlarms(): Boolean {
    return this == Intent.ACTION_BOOT_COMPLETED
}
