package dev.sayed.mehrabalmomen.presentation.utils

import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

actual fun getCurrentHijriDate(): HijriDate {
    return try {
        val hijrahDate = HijrahDate.now()
        HijriDate(
            day = hijrahDate.get(ChronoField.DAY_OF_MONTH),
            month = hijrahDate.get(ChronoField.MONTH_OF_YEAR) - 1,
            year = hijrahDate.get(ChronoField.YEAR)
        )
    } catch (_: Throwable) {
        HijriDate(day = 1, month = 0, year = 1446)
    }
}
