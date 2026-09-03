package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import platform.Foundation.*

actual fun getHijriDateString(language: AppSettings.Language): String {
    val islamicCalendar = NSCalendar(NSCalendarIdentifierIslamicUmmAlQura)
    val components = islamicCalendar.components(
        NSCalendarUnitDay or NSCalendarUnitMonth or NSCalendarUnitYear,
        NSDate()
    )

    val day = components.day
    val month = components.month
    val year = components.year

    val monthsAr = listOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
        "جمادى الأولى", "جمادى الآخرة",
        "رجب", "شعبان", "رمضان",
        "شوال", "ذو القعدة", "ذو الحجة"
    )

    val monthsEn = listOf(
        "Muharram", "Safar", "Rabi Al-Awwal", "Rabi Al-Thani",
        "Jumada Al-Awwal", "Jumada Al-Thani",
        "Rajab", "Shaban", "Ramadan",
        "Shawwal", "Dhul Qadah", "Dhul Hijjah"
    )

    val monthName = if (language == AppSettings.Language.ARABIC) {
        monthsAr.getOrNull(month.toInt() - 1) ?: ""
    } else {
        monthsEn.getOrNull(month.toInt() - 1) ?: ""
    }

    val dayStr = day.toString().toLocalizedDigits(language)
    val yearStr = year.toString().toLocalizedDigits(language)

    return "$dayStr $monthName $yearStr"
}
