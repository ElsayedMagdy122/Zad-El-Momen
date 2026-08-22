package dev.sayed.mehrabalmomen.domain.entity.widget.prayer

/** Describes a controlled failure that prevents the prayer widget from showing content. */
enum class PrayerWidgetError {
    /** The saved latitude or longitude is outside the supported geographic range. */
    INVALID_LOCATION,

    /** Valid input was available, but a usable prayer schedule could not be calculated. */
    CALCULATION_FAILED,

    /** An unexpected settings, time, or infrastructure failure occurred. */
    UNKNOWN,
}
