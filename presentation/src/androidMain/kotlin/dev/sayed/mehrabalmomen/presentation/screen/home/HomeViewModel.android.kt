package dev.sayed.mehrabalmomen.presentation.screen.home

import com.github.msarhan.ummalqura.calendar.UmmalquraGregorianConverter
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

actual fun getHijriDateString(language: AppSettings.Language): String {
    return try {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val hDateInfo = UmmalquraGregorianConverter.toHijri(today.year, today.month.ordinal + 1, today.day)
        val day = hDateInfo[2]
        val month = hDateInfo[1]
        val year = hDateInfo[0]

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
            monthsAr.getOrElse(month) { "" }
        } else {
            monthsEn.getOrElse(month) { "" }
        }

        val dayStr = day.toString().toLocalizedDigits(language)
        val yearStr = year.toString().toLocalizedDigits(language)

        "$dayStr $monthName $yearStr"
    } catch (_: Exception) {
        if (language == AppSettings.Language.ARABIC) "1448 هـ" else "1448 AH"
    }
}
