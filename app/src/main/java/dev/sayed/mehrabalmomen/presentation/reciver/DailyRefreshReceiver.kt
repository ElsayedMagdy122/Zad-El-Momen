package dev.sayed.mehrabalmomen.presentation.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class DailyRefreshReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("AZAN_DEBUG", "Daily refresh triggered for prayers and reminders")
            try {
                val prayerUseCase = GlobalContext.get().get<PrayerSchedulingUseCase>()
                prayerUseCase.rescheduleTodayPrayerAlarms()

                val reminderScheduler = GlobalContext.get().get<ReminderSchedulerRepository>()
                reminderScheduler.rescheduleAll()

                Log.d("AZAN_DEBUG", "Daily refresh successfully finished")
            } catch (e: Exception) {
                Log.e("AZAN_DEBUG", "Error during daily rollover refresh", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}