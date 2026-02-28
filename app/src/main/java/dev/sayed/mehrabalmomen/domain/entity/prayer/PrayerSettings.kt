package dev.sayed.mehrabalmomen.domain.entity.prayer

import dev.sayed.mehrabalmomen.domain.entity.location.Location

data class PrayerSettings(
    val madhab: Madhab,
    val calculationMethod: CalculationMethod,
    val location: Location
)