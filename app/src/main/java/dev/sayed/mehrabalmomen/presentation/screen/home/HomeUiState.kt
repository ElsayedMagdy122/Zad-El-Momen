package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.annotation.StringRes

data class HomeUiState(
    val location : LocationUiState = LocationUiState(),
    val time: TimeUiState = TimeUiState(),
    val prayers: List<HomeUiState.PrayerUiState> = emptyList(),
) {
    data class LocationUiState(
        val country: String = "",
        val city: String = "",

        )
    data class PrayerUiState(
        @param:StringRes
        val name: Int = 0,
        val time: String = "",
        val isUpComing: Boolean = false,
        val icon: Int = 0,
    )
    data class TimeUiState(
        val hours: String = "",
        val minutes: String = "",
        val seconds: String = "",
    )
}
