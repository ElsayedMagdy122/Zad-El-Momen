package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.repository.platform.WidgetSync

class AndroidWidgetSync : WidgetSync {
    override fun updatePrayerTimes(
        hijriDate: String,
        nextPrayerName: String,
        nextPrayerTime: String,
        prayers: Map<String, String>
    ) {
        // Android widget is updated via AppWidgetProvider
    }
}
