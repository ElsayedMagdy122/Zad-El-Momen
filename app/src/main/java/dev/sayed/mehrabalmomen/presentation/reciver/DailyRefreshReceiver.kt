package dev.sayed.mehrabalmomen.presentation.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class DailyRefreshReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("AZAN_DEBUG", "Daily refresh triggered")
            try {
                val useCase = GlobalContext.get().get<PrayerSchedulingUseCase>()
                useCase.rescheduleTodayPrayerAlarms()
                Log.d("AZAN_DEBUG", "Daily refresh finished")
            } finally {
                pendingResult.finish()
            }
        }
    }
}