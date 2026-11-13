package dev.sayed.mehrabalmomen.domain.entity.prayer

data class PrayerAlarm(
    val id: Int,
    val name: Prayer.PrayerName,
    val timeMillis: Long,
    val enabled: Boolean
)