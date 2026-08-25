package dev.sayed.mehrabalmomen.presentation.screen.radio

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

sealed interface  RadioChannelsEffect {
    data class ShowToast(
        val toast: ToastDetails
    ) : RadioChannelsEffect
    data class PlaySound(val url:String,val titleText:String) : RadioChannelsEffect
   object PauseSound : RadioChannelsEffect
}