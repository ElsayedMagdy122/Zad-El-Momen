package dev.sayed.mehrabalmomen.presentation.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import dev.sayed.mehrabalmomen.R

class PrayerTimesWidgetPinReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        PrayerTimesWidgetProvider.updateAllWidgets(context)
        Toast.makeText(
            context,
            context.getString(R.string.widget_pin_success),
            Toast.LENGTH_SHORT
        ).show()
    }
}
