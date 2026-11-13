package dev.sayed.mehrabalmomen.presentation.screen.bookmarks

import androidx.compose.runtime.Composable
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.domain.entity.quran.bookmark.Bookmark
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedPlural
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.utils.toLocalizedDigits
import dev.sayed.mehrabalmomen.domain.utils.getCurrentTimeMillis

fun Bookmark.toUi(): BookmarkListUiState.AyahHistoryUi {
    return BookmarkListUiState.AyahHistoryUi(
        surahId = surahId,
        ayahId = ayahId,
        englishName = englishName,
        arabicName = arabicName,
        ayahNumber = ayahId,
        ayahText = text,
        timeAgo = bookmarkedAt
    )
}

fun List<Bookmark>.toUi(): List<BookmarkListUiState.AyahHistoryUi> {
    return map { it.toUi() }
}

@Composable
fun Long.toTimeAgo(): String {
    val diff = (getCurrentTimeMillis() - this).coerceAtLeast(0L)
    val seconds = diff / 1000L
    val minutes = seconds / 60L
    val hours = minutes / 60L
    val days = hours / 24L
    val months = days / 30L
    val years = days / 365L
    val language = LocalAppLocale.current

    val rawText = when {
        years > 0 -> localizedPlural(Res.plurals.years_ago, years.toInt(), years.toInt())
        months > 0 -> localizedPlural(Res.plurals.months_ago, months.toInt(), months.toInt())
        days > 0 -> localizedPlural(Res.plurals.days_ago, days.toInt(), days.toInt())
        hours > 0 -> localizedPlural(Res.plurals.hours_ago, hours.toInt(), hours.toInt())
        minutes > 0 -> localizedPlural(Res.plurals.minutes_ago, minutes.toInt(), minutes.toInt())
        seconds > 0 -> localizedPlural(Res.plurals.seconds_ago, seconds.toInt(), seconds.toInt())
        else -> localizedString(Res.string.just_now)
    }
    return rawText.toLocalizedDigits(language)
}
