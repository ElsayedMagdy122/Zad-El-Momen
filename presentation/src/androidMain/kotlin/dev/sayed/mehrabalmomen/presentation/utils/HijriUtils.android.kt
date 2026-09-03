package dev.sayed.mehrabalmomen.presentation.utils

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import java.util.Calendar

actual fun getHijriDateString(language: AppSettings.Language): String {
    val calendar = UmmalquraCalendar()

    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)

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

    val monthName =
        if (language == AppSettings.Language.ARABIC) monthsAr[month]
        else monthsEn[month]

    val dayStr = day.toString().toLocalizedDigits(language)
    val yearStr = year.toString().toLocalizedDigits(language)

    return "$dayStr $monthName $yearStr"
}
