package dev.sayed.mehrabalmomen.presentation.screen.qiblah

data class QiblahUiState(
    val qiblaBearing: Float = 0.0f,
    val deviceAzimuth: Float = 0.0f,
    val relativeAngle: Float = 0.0f,
    val qiblaOffset: Float = 0.0f,
    val isAligned: Boolean = false,
    val location: LocationUiState = LocationUiState(),
    val isCompassAvailable: Boolean = true
) {
    data class LocationUiState(
        val country: String = "Unknown",
        val city: String = "Unknown",
    )
}
