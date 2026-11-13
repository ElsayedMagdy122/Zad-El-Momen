package dev.sayed.mehrabalmomen.presentation.utils

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierIslamicUmmAlQura
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate

actual fun getCurrentHijriDate(): HijriDate {
    return try {
        val calendar = NSCalendar.calendarWithIdentifier(NSCalendarIdentifierIslamicUmmAlQura)
            ?: NSCalendar.currentCalendar
        val components = calendar.components(
            NSCalendarUnitDay or NSCalendarUnitMonth or NSCalendarUnitYear,
            fromDate = NSDate()
        )
        HijriDate(
            day = components.day.toInt(),
            month = (components.month.toInt() - 1).coerceAtLeast(0),
            year = components.year.toInt()
        )
    } catch (_: Throwable) {
        HijriDate(day = 1, month = 0, year = 1446)
    }
}
