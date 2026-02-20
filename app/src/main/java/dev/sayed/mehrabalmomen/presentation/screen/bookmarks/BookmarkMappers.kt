package dev.sayed.mehrabalmomen.presentation.screen.bookmarks

import androidx.compose.runtime.Composable
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.entity.Bookmark
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedPlural
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import java.util.concurrent.TimeUnit

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

    val diff = System.currentTimeMillis() - this
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    val months = days / 30
    val years = days / 365
    val language = LocalAppLocale.current
    val rawText = when {
        years > 0 -> localizedPlural(R.plurals.years_ago, years.toInt(), years.toInt())
        months > 0 -> localizedPlural(R.plurals.months_ago, months.toInt(), months.toInt())
        days > 0 -> localizedPlural(R.plurals.days_ago, days.toInt(), days.toInt())
        hours > 0 -> localizedPlural(R.plurals.hours_ago, hours.toInt(), hours.toInt())
        minutes > 0 -> localizedPlural(R.plurals.minutes_ago, minutes.toInt(), minutes.toInt())
        seconds > 0 -> localizedPlural(R.plurals.seconds_ago, seconds.toInt(), seconds.toInt())
        else -> localizedString(R.string.just_now)
    }
    return rawText.toLocalizedDigits(language)
}