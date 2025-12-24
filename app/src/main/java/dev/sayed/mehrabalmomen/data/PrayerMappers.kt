@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.data

import com.batoulapps.adhan2.PrayerTimes
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun PrayerTimes.toPrayerList(): List<Prayer> {
    return Prayer.PrayerName.entries.map { name ->
        Prayer(
            name = name,
            time = this.toPrayerTime(name)
        )
    }
}

fun PrayerTimes.toPrayerTime(name: Prayer.PrayerName): Instant {
    return when (name) {
        Prayer.PrayerName.FAJR -> fajr
        Prayer.PrayerName.ZUHR -> dhuhr
        Prayer.PrayerName.ASR -> asr
        Prayer.PrayerName.MAGHRIB -> maghrib
        Prayer.PrayerName.ISHA -> isha
    }
}

fun com.batoulapps.adhan2.Prayer.toDomainName(): Prayer.PrayerName {
    return when (this) {
        com.batoulapps.adhan2.Prayer.FAJR -> Prayer.PrayerName.FAJR
        com.batoulapps.adhan2.Prayer.DHUHR -> Prayer.PrayerName.ZUHR
        com.batoulapps.adhan2.Prayer.ASR -> Prayer.PrayerName.ASR
        com.batoulapps.adhan2.Prayer.MAGHRIB -> Prayer.PrayerName.MAGHRIB
        com.batoulapps.adhan2.Prayer.ISHA -> Prayer.PrayerName.ISHA
        else -> throw IllegalArgumentException("Unsupported prayer")
    }
}