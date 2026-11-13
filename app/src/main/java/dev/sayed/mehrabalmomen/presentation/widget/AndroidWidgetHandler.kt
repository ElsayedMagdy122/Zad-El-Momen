package dev.sayed.mehrabalmomen.presentation.widget

import android.content.Context
import dev.sayed.mehrabalmomen.presentation.utils.WidgetHandler

class AndroidWidgetHandler(
    private val context: Context
) : WidgetHandler {

    override fun updateWidgets() {
        PrayerTimesWidgetProvider.updateAllWidgets(context)
    }

    override fun requestPinWidget(onFallback: () -> Unit) {
        onFallback()
    }
}
