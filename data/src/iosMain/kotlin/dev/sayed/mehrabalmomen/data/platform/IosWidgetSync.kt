package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.repository.platform.WidgetSync
import platform.Foundation.NSUserDefaults

class IosWidgetSync : WidgetSync {
    override fun updatePrayerTimes(
        hijriDate: String,
        nextPrayerName: String,
        nextPrayerTime: String,
        prayers: Map<String, String>
    ) {
        val sharedDefaults = NSUserDefaults(suiteName = "group.dev.sayed.mehrabalmomen") ?: return
        sharedDefaults.setObject(hijriDate, forKey = "widget_hijri_date")
        sharedDefaults.setObject(nextPrayerName, forKey = "widget_next_prayer")
        sharedDefaults.setObject(nextPrayerTime, forKey = "widget_next_time")

        prayers["fajr"]?.let { sharedDefaults.setObject(it, forKey = "p_fajr") }
        prayers["dhuhr"]?.let { sharedDefaults.setObject(it, forKey = "p_dhuhr") }
        prayers["asr"]?.let { sharedDefaults.setObject(it, forKey = "p_asr") }
        prayers["maghrib"]?.let { sharedDefaults.setObject(it, forKey = "p_maghrib") }
        prayers["isha"]?.let { sharedDefaults.setObject(it, forKey = "p_isha") }

        sharedDefaults.synchronize()
    }
}
