package dev.sayed.mehrabalmomen.presentation.screen.ReportBug

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import org.jetbrains.compose.resources.StringResource

data class ReportBugUiState(
    val title: String = "",
    val description: String = "",
    val feature: FeatureArea = FeatureArea.PRAYER_TIMES,
    val imageUrl: String? = null,
    val isLoading: Boolean = false
)

enum class FeatureArea(
    val labelRes: StringResource
) {
    PRAYER_TIMES(Res.string.prayer_times),
    QURAN(Res.string.quran),
    QIBLA(Res.string.qiblah),
    ADHKAR(Res.string.azkar),
    RADIO(Res.string.quran_radio),
    OTHER(Res.string.other)
}
