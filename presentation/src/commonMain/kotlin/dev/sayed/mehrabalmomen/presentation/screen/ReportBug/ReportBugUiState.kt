package dev.sayed.mehrabalmomen.presentation.screen.ReportBug

data class ReportBugUiState(
    val title: String = "",
    val description: String = "",
    val feature: FeatureArea = FeatureArea.PRAYER_TIMES,
    val imageUrl: String? = null,
    val isLoading: Boolean = false
)

enum class FeatureArea(val label: String) {
    PRAYER_TIMES("Prayer Times"),
    QURAN("Quran"),
    QIBLA("Qiblah"),
    ADHKAR("Azkar"),
    RADIO("Radio"),
    OTHER("Other")
}
