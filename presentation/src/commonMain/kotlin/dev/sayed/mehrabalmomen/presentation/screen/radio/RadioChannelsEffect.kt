package dev.sayed.mehrabalmomen.presentation.screen.radio

sealed interface RadioChannelsEffect {
    data class ShowToast(val message: String) : RadioChannelsEffect
    data class PlaySound(val url: String, val titleText: String) : RadioChannelsEffect
    object PauseSound : RadioChannelsEffect
}
