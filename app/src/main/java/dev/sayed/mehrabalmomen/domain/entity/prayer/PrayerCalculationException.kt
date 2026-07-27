package dev.sayed.mehrabalmomen.domain.entity.prayer

/**
 * Controlled exception returned when coordinates or Adhan2 calculation output are unusable.
 *
 * @property error machine-readable failure reason for presentation-layer error mapping.
 * @param message developer-facing explanation of the failed validation or calculation.
 * @param cause optional library exception that caused the calculation to fail.
 */
class PrayerCalculationException(
    val error: PrayerCalculationError,
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
