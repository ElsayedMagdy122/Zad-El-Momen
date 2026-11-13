package dev.sayed.mehrabalmomen.presentation.screen.bookmarks

data class BookmarkListUiState(
    val history: List<AyahHistoryUi> = emptyList(),
    val isLoading: Boolean = false,
) {
    data class AyahHistoryUi(
        val surahId: Int,
        val ayahId: Int,
        val englishName: String,
        val arabicName: String,
        val ayahNumber: Int,
        val ayahText: String,
        val timeAgo: Long
    )
}


