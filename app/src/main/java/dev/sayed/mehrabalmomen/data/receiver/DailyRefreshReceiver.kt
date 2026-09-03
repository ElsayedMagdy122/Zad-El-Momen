package dev.sayed.mehrabalmomen.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import dev.sayed.mehrabalmomen.domain.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class DailyRefreshReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        val koin = GlobalContext.get()
        val logger = koin.get<Logger>()
        
        CoroutineScope(Dispatchers.IO).launch {
            logger.d("AZAN_DEBUG", "Daily refresh triggered for prayers and reminders")
            try {
                val prayerUseCase = koin.get<PrayerSchedulingUseCase>()
                prayerUseCase.rescheduleTodayPrayerAlarms()

                val reminderScheduler = koin.get<ReminderSchedulerRepository>()
                reminderScheduler.rescheduleAll()

                logger.d("AZAN_DEBUG", "Daily refresh successfully finished")
            } catch (e: Exception) {
                logger.e("AZAN_DEBUG", "Error during daily rollover refresh", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}