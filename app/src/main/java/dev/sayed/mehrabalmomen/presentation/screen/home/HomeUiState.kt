package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.annotation.StringRes

data class HomeUiState(
    val location: LocationUiState = LocationUiState(),
    val lastTilawahUi: ContinueTilawahUi = ContinueTilawahUi(),
    val time: TimeUiState = TimeUiState(),
    val prayers: List<PrayerUiState> = emptyList(),
    val nextPrayer: PrayerUiState = PrayerUiState()
) {
    data class LocationUiState(
        val country: String = "",
        val city: String = "",

        )

    data class PrayerUiState(
        @param:StringRes
        val name: Int = 0,
        val time: String = "00 : 00 : 00",
        val isUpComing: Boolean = false,
        val icon: Int = 0,
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
