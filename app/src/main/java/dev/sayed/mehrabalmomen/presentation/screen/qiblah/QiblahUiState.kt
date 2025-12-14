package dev.sayed.mehrabalmomen.presentation.screen.qiblah

data class QiblahUiState(
    val direction: Float = 0.0f,
    val location: LocationUiState = LocationUiState(),
) {
    data class LocationUiState(
        val country: String = "Unknown",
        val city: String = "Unknown",
    )
}
