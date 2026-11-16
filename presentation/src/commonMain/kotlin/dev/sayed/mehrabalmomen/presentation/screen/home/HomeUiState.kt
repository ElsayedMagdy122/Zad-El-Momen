package dev.sayed.mehrabalmomen.presentation.screen.home

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.base.UiIcon
import dev.sayed.mehrabalmomen.presentation.base.UiText
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.ic_azan_selected

data class HomeUiState(
    val location: LocationUiState = LocationUiState(),
    val lastTilawahUi: ContinueTilawahUi = ContinueTilawahUi(),
    val time: TimeUiState = TimeUiState(),
    val prayers: List<PrayerUiState> = emptyList(),
    val nextPrayer: PrayerUiState = PrayerUiState(),
    val hijriDate: String = ""
) {
    data class LocationUiState(
        val country: String = "",
        val city: String = "",
    )

    data class PrayerUiState(
        val prayerName: Prayer.PrayerName? = null,
        val name: UiText = UiText.DynamicString(""),
        val time: String = "00 : 00 : 00",
        val isUpComing: Boolean = false,
        val icon: UiIcon = UiIcon(Res.drawable.ic_azan_selected),
        val isAm : Boolean = false
    )

    data class TimeUiState(
        val hours: String = "00",
        val minutes: String = "00",
        val seconds: String = "00",
    )

    data class ContinueTilawahUi(
        val surahName: String = "",
        val ayahNumber: Int = 1,
        val surahId: Int = 1
    )
}
