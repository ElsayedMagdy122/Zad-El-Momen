package dev.sayed.mehrabalmomen.domain.repository.platform

interface WidgetSync {
    fun updatePrayerTimes(
        hijriDate: String,
        nextPrayerName: String,
        nextPrayerTime: String,
        prayers: Map<String, String>
    )
}
