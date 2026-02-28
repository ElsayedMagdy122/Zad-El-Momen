package dev.sayed.mehrabalmomen.domain.model

import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.Madhab

data class PrayerSettings(
    val madhab: Madhab,
    val calculationMethod: CalculationMethod,
    val location: Location
)