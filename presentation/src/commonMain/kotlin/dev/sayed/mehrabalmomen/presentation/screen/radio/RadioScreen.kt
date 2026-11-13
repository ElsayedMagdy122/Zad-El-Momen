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
import dev.sayed.mehrabalmomen.presentation.screen.radio.components.RadioContent
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

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
expect fun HandleRadioEffects(
    viewModel: RadioChannelsViewModel,
    onToast: (ToastDetails) -> Unit
)

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
