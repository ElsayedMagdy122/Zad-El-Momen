package dev.sayed.mehrabalmomen.data.prayer.repository

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.model.RescheduleResult
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerAlarmRepository
import dev.sayed.mehrabalmomen.presentation.reciver.AzanAlarmReceiver
import dev.sayed.mehrabalmomen.presentation.reciver.DailyRefreshReceiver
import dev.sayed.mehrabalmomen.presentation.utils.AlarmScheduler
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_NAME_KEY
import java.util.Calendar

class PrayerAlarmRepositoryImpl(
    private val context: Context,
    private val alarmScheduler: AlarmScheduler
) : PrayerAlarmRepository {

    override fun reschedule(prayers: List<PrayerAlarm>): RescheduleResult {

        if (!hasExactAlarmPermission()) {
            return RescheduleResult.PermissionRequired
        }

        scheduleEnabledPrayerAlarms(prayers)
        scheduleMidnight()
        return RescheduleResult.Success
    }

    private fun hasExactAlarmPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return context
            .getSystemService(AlarmManager::class.java)
            .canScheduleExactAlarms()
    }

    private fun scheduleEnabledPrayerAlarms(prayers: List<PrayerAlarm>) {
        prayers.forEach { prayer ->
            val intent = Intent(context, AzanAlarmReceiver::class.java)
                .putExtra(PRAYER_NAME_KEY, prayer.name.name)
            alarmScheduler.cancel(prayer.id, intent)
        }

        prayers.filter { it.enabled }.forEach { prayer ->
            val intent = Intent(context, AzanAlarmReceiver::class.java)
                .putExtra(PRAYER_NAME_KEY, prayer.name.name)
            alarmScheduler.scheduleExact(prayer.id, prayer.timeMillis, intent)
        }
    }

    private fun scheduleMidnight() {
        val intent = Intent(context, DailyRefreshReceiver::class.java)

        alarmScheduler.scheduleExact(
            MIDNIGHT_ROLLOVER_REQUEST_CODE,
            calculateNextMidnightMillis(),
            intent
        )
    }

    private fun calculateNextMidnightMillis(): Long =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private companion object {
        const val MIDNIGHT_ROLLOVER_REQUEST_CODE = 99
    }
}