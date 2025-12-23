package dev.sayed.mehrabalmomen.data.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sayed.mehrabalmomen.data.AzanManager
import org.koin.core.context.GlobalContext

class MidnightRolloverReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val azanManager = GlobalContext.get().get<AzanManager>()
        azanManager.reschedule()
    }
}