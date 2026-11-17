package dev.sayed.mehrabalmomen.presentation.screen.radio

import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.presentation.screen.radio.components.RadioContent
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerAction
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerService
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.ACTION_SENDED
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.NOTIFICATION_TITLE
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.STREAM_URL
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun RadioScreen(
    navController: NavController,
    viewModel: RadioChannelsViewModel
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

    RadioContent(
        state = state,
        viewModel = viewModel,
        toast = toast
    )
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
                val intent = Intent(context, RadioPlayerService::class.java).apply {
                    putExtra(STREAM_URL, effect.url)
                    putExtra(NOTIFICATION_TITLE, effect.titleText)
                    putExtra(ACTION_SENDED, RadioPlayerAction.PLAY.name)
                }
                context.startService(intent)
            }
            is RadioChannelsEffect.PauseSound -> {
                val intent = Intent(context, RadioPlayerService::class.java).apply {
                    putExtra(ACTION_SENDED, RadioPlayerAction.PAUSE.name)
                }
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
