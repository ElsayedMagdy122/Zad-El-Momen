package dev.sayed.mehrabalmomen.domain.entity.prayer

/** Identifies a controlled failure at the prayer-calculation repository boundary. */
enum class PrayerCalculationError {
    INVALID_LATITUDE,
    INVALID_LONGITUDE,
    CALCULATION_FAILED,
    INVALID_PRAYER_RESULT,
}
