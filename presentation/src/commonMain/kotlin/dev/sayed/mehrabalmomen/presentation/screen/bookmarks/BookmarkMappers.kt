package dev.sayed.mehrabalmomen.presentation.screen.bookmarks

import androidx.compose.runtime.Composable
import dev.sayed.mehrabalmomen.domain.entity.quran.bookmark.Bookmark
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.just_now

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
    val diff = kotlin.time.Clock.System.now().toEpochMilliseconds() - this
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val language = LocalAppLocale.current
    val rawText = when {
        days > 0 -> "$days days ago"
        hours > 0 -> "$hours hours ago"
        minutes > 0 -> "$minutes minutes ago"
        else -> localizedString(Res.string.just_now)
    }
    return rawText.toLocalizedDigits(language)
}
