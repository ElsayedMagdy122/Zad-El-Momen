package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.presentation.screen.radio.components.RadioContent
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
actual fun RadioScreen(
    navController: NavController,
    viewModel: RadioChannelsViewModel
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var toast by remember { mutableStateOf<ToastDetails?>(null) }
    val audioPlayer: AudioPlayer = koinInject(named("radio"))

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is RadioChannelsEffect.ShowToast -> {
                toast = effect.toast
            }
            is RadioChannelsEffect.PlaySound -> {
                audioPlayer.play(AudioSource.RemoteUrl(effect.url))
            }
            is RadioChannelsEffect.PauseSound -> {
                audioPlayer.pause()
            }
        }
    }

    LaunchedEffect(toast) {
        toast?.let {
            val current = it
            delay(3000)
            if (toast == current) {
                toast = null
            }
        }
    }

    RadioContent(
        state = state,
        viewModel = viewModel,
        toast = toast
    )
}
