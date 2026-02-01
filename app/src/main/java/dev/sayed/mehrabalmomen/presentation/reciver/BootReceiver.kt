package dev.sayed.mehrabalmomen.presentation.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext


class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val useCase = GlobalContext.get().get<PrayerSchedulingUseCase>()
                useCase.rescheduleTodayPrayerAlarms()
            } finally {
                pendingResult.finish()
            }
        }
    }
}