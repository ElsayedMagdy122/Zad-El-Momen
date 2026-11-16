package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.presentation.screen.radio.components.RadioContent
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@Composable
fun RadioScreen(
    navController: NavController,
    viewModel: RadioChannelsViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var toast by remember { mutableStateOf<ToastDetails?>(null) }

    HandleRadioEffects(viewModel) {
        toast = it
    }

    HandleToastAutoDismiss(toast) {
        toast = null
    }

    RadioContent(
        state = state,
        viewModel = viewModel,
        toast = toast
    )
}

@Composable
private fun HandleRadioEffects(
    viewModel: RadioChannelsViewModel,
    onToast: (ToastDetails) -> Unit
) {
    val audioPlayer: AudioPlayer = koinInject(named("radio"))

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is RadioChannelsEffect.ShowToast -> {
                onToast(effect.toast)
            }

            is RadioChannelsEffect.PlaySound -> {
                audioPlayer.play(AudioSource.RemoteUrl(effect.url))
            }

            is RadioChannelsEffect.PauseSound -> {
                audioPlayer.pause()
            }
        }
    }
}

@Composable
private fun HandleToastAutoDismiss(
    toast: ToastDetails?,
    onDismiss: () -> Unit
) {
    LaunchedEffect(toast) {
        toast?.let {
            val current = it
            delay(3000)
            if (toast == current) {
                onDismiss()
            }
        }
    }
}
