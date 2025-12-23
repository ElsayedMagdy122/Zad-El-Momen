package dev.sayed.mehrabalmomen.domain.model

import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Madhab

data class AppSettings(
    val madhab: Madhab,
    val calculationMethod: CalculationMethod,
    val latitude: Double,
    val longitude: Double,
    val alarmsScheduled: Boolean
)