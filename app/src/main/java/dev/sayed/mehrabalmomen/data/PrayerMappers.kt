@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.data

import com.batoulapps.adhan2.PrayerTimes
import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun PrayerTimes.toDomain(): List<Prayer> {
    return Prayer.PrayerName.entries.map { name ->
        Prayer(
            name = name,
            time = this.toPrayerTime(name)
        )
    }
}

fun PrayerTimes.toDomainPrayer(prayerName: Prayer.PrayerName): Prayer {
    return Prayer(
        name = prayerName,
        time = this.toPrayerTime(prayerName)
    )
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
fun CalculationMethod.toAdhanCalculationMethod():  com.batoulapps.adhan2.CalculationMethod  {
    return when (this) {
        CalculationMethod.MUSLIM_WORLD_LEAGUE -> com.batoulapps.adhan2.CalculationMethod.MUSLIM_WORLD_LEAGUE
        CalculationMethod.EGYPTIAN -> com.batoulapps.adhan2.CalculationMethod.EGYPTIAN
        CalculationMethod.KARACHI -> com.batoulapps.adhan2.CalculationMethod.KARACHI
        CalculationMethod.UMM_AL_QURA -> com.batoulapps.adhan2.CalculationMethod.UMM_AL_QURA
        CalculationMethod.DUBAI -> com.batoulapps.adhan2.CalculationMethod.DUBAI
        CalculationMethod.QATAR -> com.batoulapps.adhan2.CalculationMethod.QATAR
        CalculationMethod.KUWAIT -> com.batoulapps.adhan2.CalculationMethod.KUWAIT
        CalculationMethod.MOONSIGHTING_COMMITTEE -> com.batoulapps.adhan2.CalculationMethod.MOON_SIGHTING_COMMITTEE
        CalculationMethod.SINGAPORE -> com.batoulapps.adhan2.CalculationMethod.SINGAPORE
        CalculationMethod.NORTH_AMERICA -> com.batoulapps.adhan2.CalculationMethod.NORTH_AMERICA
        CalculationMethod.OTHER -> com.batoulapps.adhan2.CalculationMethod.OTHER
    }
}