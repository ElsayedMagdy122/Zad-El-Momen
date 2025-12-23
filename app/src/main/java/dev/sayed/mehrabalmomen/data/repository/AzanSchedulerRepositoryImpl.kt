package dev.sayed.mehrabalmomen.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import dev.sayed.mehrabalmomen.data.AlarmScheduler
import dev.sayed.mehrabalmomen.data.ExactAlarmPermissionDataSource
import dev.sayed.mehrabalmomen.data.reciver.AzanAlarmReceiver
import dev.sayed.mehrabalmomen.data.reciver.MidnightRolloverReceiver
import dev.sayed.mehrabalmomen.domain.model.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.repository.AzanSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.RescheduleResult
import java.util.Calendar

class AzanSchedulerRepositoryImpl(
    private val context: Context,
    private val permission: ExactAlarmPermissionDataSource,
    private val alarmScheduler: AlarmScheduler
) : AzanSchedulerRepository {

    override fun reschedule(prayers: List<PrayerAlarm>): RescheduleResult {

        if (!permission.hasPermission()) {
            return RescheduleResult.PermissionRequired
        }

        schedulePrayerAlarms(prayers)
        scheduleMidnight()
        return RescheduleResult.Success
    }

//    private fun schedulePrayerAlarms(prayers: List<PrayerAlarm>) {
//        prayers.forEach { prayer ->
//            val intent = Intent(context, AzanAlarmReceiver::class.java)
//                .putExtra("PRAYER_NAME", prayer.name.name)
//
//            if (prayer.enabled) {
//                alarmScheduler.scheduleExact(
//                    prayer.id,
//                    prayer.timeMillis,
//                    intent
//                )
//            } else {
//                alarmScheduler.cancel(prayer.id, intent)
//            }
//        }
//    }
    private fun schedulePrayerAlarms(prayers: List<PrayerAlarm>) {
        prayers.forEach { prayer ->
            val intent = Intent(context, AzanAlarmReceiver::class.java)
                .putExtra("PRAYER_NAME", prayer.name.name)
            alarmScheduler.cancel(prayer.id, intent)
        }

        prayers.filter { it.enabled }.forEach { prayer ->
            val intent = Intent(context, AzanAlarmReceiver::class.java)
                .putExtra("PRAYER_NAME", prayer.name.name)
            Log.d("AzanSchedulerRepository", "Scheduling alarm for prayer: ${prayer.name}")
            alarmScheduler.scheduleExact(prayer.id, prayer.timeMillis, intent)
        }
    }

    private fun scheduleMidnight() {
        val intent = Intent(context, MidnightRolloverReceiver::class.java)

        alarmScheduler.scheduleExact(
            99,
            nextMidnightMillis(),
            intent
        )
    }

    private fun nextMidnightMillis(): Long =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}