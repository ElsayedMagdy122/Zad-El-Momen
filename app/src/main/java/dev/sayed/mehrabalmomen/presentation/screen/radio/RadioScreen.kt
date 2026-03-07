package dev.sayed.mehrabalmomen.presentation.screen.radio

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.presentation.screen.radio.components.RadioContent
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.AudioPlayerAction
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.AudioPlayerService
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerConstants.ACTION_SENDED
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerConstants.NOTIFICATION_TITLE
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerConstants.STREAM_URL
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel


@Composable
fun RadioScreen(
    navController: NavController,
    viewModel: RadioChannelsViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    var toast by remember { mutableStateOf<ToastDetails?>(null) }

    HandleRadioEffects(viewModel, context) {
        toast = it
    }

    HandleToastAutoDismiss(toast) {
        toast = null
    }

    Crossfade(targetState = state) { state ->
        RadioContent(
            state = state,
            viewModel = viewModel,
            toast = toast
        )
    }
}

@Composable
private fun HandleRadioEffects(
    viewModel: RadioChannelsViewModel,
    context: android.content.Context,
    onToast: (ToastDetails) -> Unit
) {

    CollectEffect(viewModel.effect) { effect ->

        when (effect) {

            is RadioChannelsEffect.ShowToast -> {
                onToast(effect.toast)
            }

            is RadioChannelsEffect.PlaySound -> {

                val intent = Intent(context, AudioPlayerService::class.java)

                intent.putExtra(STREAM_URL, effect.url)
                intent.putExtra(NOTIFICATION_TITLE, effect.titleText)
                intent.putExtra(ACTION_SENDED, AudioPlayerAction.PLAY.name)

                context.startService(intent)
            }

            is RadioChannelsEffect.PauseSound -> {

                val intent = Intent(context, AudioPlayerService::class.java)

                intent.putExtra(ACTION_SENDED, AudioPlayerAction.PAUSE.name)

                context.startService(intent)
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