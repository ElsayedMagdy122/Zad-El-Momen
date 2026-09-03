package dev.sayed.mehrabalmomen.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prayerUseCase = GlobalContext.get().get<PrayerSchedulingUseCase>()
                prayerUseCase.rescheduleTodayPrayerAlarms()
                val reminderScheduler = GlobalContext.get().get<ReminderSchedulerRepository>()
                reminderScheduler.rescheduleAll()
            } finally {
                pendingResult.finish()
            }
        }
    }
}