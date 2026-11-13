package dev.sayed.mehrabalmomen.presentation.screen.radio

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerAction
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.ACTION_SENDED
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.NOTIFICATION_TITLE
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.STREAM_URL
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect

@Composable
actual fun HandleRadioEffects(
    viewModel: RadioChannelsViewModel,
    onToast: (ToastDetails) -> Unit
) {
    val context = LocalContext.current

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is RadioChannelsEffect.ShowToast -> {
                onToast(effect.toast)
            }
            is RadioChannelsEffect.PlaySound -> {
                val intent = Intent().apply {
                    component = ComponentName(
                        context.packageName,
                        "dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerService"
                    )
                    putExtra(STREAM_URL, effect.url)
                    putExtra(NOTIFICATION_TITLE, effect.titleText)
                    putExtra(ACTION_SENDED, RadioPlayerAction.PLAY.name)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
            is RadioChannelsEffect.PauseSound -> {
                val intent = Intent().apply {
                    component = ComponentName(
                        context.packageName,
                        "dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerService"
                    )
                    putExtra(ACTION_SENDED, RadioPlayerAction.PAUSE.name)
                }
                context.startService(intent)
            }
        }
    }
}
