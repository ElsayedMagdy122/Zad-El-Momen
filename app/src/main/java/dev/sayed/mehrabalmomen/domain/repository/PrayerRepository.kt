package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface PrayerRepository {
     fun getDailyPrayers(
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate
    ): List<Prayer>

    @OptIn(ExperimentalTime::class)
    fun getNextPrayer(
        instant: Instant,
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate
    ): Prayer

}