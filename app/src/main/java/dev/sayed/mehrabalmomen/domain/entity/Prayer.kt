package dev.sayed.mehrabalmomen.domain.entity

import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
data class Prayer (
    val name: PrayerName,
    val time: Instant,
) {
    enum class PrayerName {
        FAJR,
        ZUHR,
        ASR,
        MAGHRIB,
        ISHA
    }
}

