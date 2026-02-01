package dev.sayed.mehrabalmomen.presentation.screen.ReportBug

import dev.sayed.mehrabalmomen.R

data class ReportBugUiState(
    val title: String = "",
    val description: String = "",
    val feature: FeatureArea = FeatureArea.PRAYER_TIMES,
    val imageUrl: String? = null,
    val isLoading: Boolean = false
)
enum class FeatureArea(
    val labelRes: Int
) {
    PRAYER_TIMES(R.string.prayer_times),
    QURAN(R.string.quran),
    QIBLA(R.string.qiblah),
    ADHKAR(R.string.azkar),
    OTHER(R.string.other)
}