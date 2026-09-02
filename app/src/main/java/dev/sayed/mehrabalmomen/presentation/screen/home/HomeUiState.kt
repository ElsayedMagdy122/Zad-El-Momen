package dev.sayed.mehrabalmomen.presentation.screen.home

import dev.sayed.mehrabalmomen.presentation.base.UiIcon
import dev.sayed.mehrabalmomen.presentation.base.UiText

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
        val name: UiText = UiText.DynamicString(""),
        val time: String = "00 : 00 : 00",
        val isUpComing: Boolean = false,
        val icon: UiIcon = UiIcon(0),
        val isAm : Boolean = false
    )

    data class TimeUiState(
        val hours: String = "00",
        val minutes: String = "00",
        val seconds: String = "00",
    )

    data class ContinueTilawahUi(
        val surahId: Int = 0,
        val nameArabic: String = "",
        val nameEnglish: String = "",
        val ayahId: Int = 0
    )
}
