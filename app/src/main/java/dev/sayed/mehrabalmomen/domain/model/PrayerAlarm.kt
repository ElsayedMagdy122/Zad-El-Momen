package dev.sayed.mehrabalmomen.domain.model

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer

data class PrayerAlarm(
    val id: Int,
    val name: Prayer.PrayerName,
    val timeMillis: Long,
    val enabled: Boolean
)