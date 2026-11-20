package dev.sayed.mehrabalmomen.presentation.screen.ReportBug

import org.jetbrains.compose.resources.StringResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.azkar
import zadelmomen.presentation.generated.resources.other
import zadelmomen.presentation.generated.resources.prayer_times
import zadelmomen.presentation.generated.resources.qiblah
import zadelmomen.presentation.generated.resources.quran
import zadelmomen.presentation.generated.resources.quran_radio

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
