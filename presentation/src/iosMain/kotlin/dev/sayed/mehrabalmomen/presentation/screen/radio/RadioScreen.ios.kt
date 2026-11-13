package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.compose.runtime.Composable
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect

@Composable
actual fun HandleRadioEffects(
    viewModel: RadioChannelsViewModel,
    onToast: (ToastDetails) -> Unit
) {
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is RadioChannelsEffect.ShowToast -> {
                onToast(effect.toast)
            }
            is RadioChannelsEffect.PlaySound -> {
                // iOS platform audio playback implementation hook
            }
            is RadioChannelsEffect.PauseSound -> {
                // iOS platform audio playback pause hook
            }
        }
    }
}
