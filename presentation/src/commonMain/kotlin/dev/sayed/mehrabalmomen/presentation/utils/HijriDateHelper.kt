package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.domain.model.AppSettings

data class HijriDate(
    val day: Int,
    val month: Int,
    val year: Int
)

expect fun getCurrentHijriDate(): HijriDate

fun formatHijriDate(hijriDate: HijriDate, language: AppSettings.Language): String {
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

    val safeMonth = hijriDate.month.coerceIn(0, 11)
    val monthName = if (language == AppSettings.Language.ARABIC) monthsAr[safeMonth] else monthsEn[safeMonth]

    val dayStr = hijriDate.day.toString().toLocalizedDigits(language)
    val yearStr = hijriDate.year.toString().toLocalizedDigits(language)

    return "$dayStr $monthName $yearStr"
}
