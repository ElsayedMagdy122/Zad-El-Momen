package dev.sayed.mehrabalmomen.presentation.screen.radio

data class RadioUiState(
    val isLoading: Boolean = true,
    val isNoInternet: Boolean=false,
    val channels: List<RadioChannelUiState> = emptyList(),
){
    data class RadioChannelUiState(
        val id: Int,
        val nameAr: String,
        val nameEn: String,
        val streamUrl: String,
        val selected: Boolean = false,
        val isPlaying: Boolean = false,
        val isLoading: Boolean = false
    )
}
