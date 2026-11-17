package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.presentation.screen.radio.components.RadioContent
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun RadioScreen(
    navController: NavController,
    viewModel: RadioChannelsViewModel
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var toast by remember { mutableStateOf<ToastDetails?>(null) }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is RadioChannelsEffect.ShowToast -> {
                toast = effect.toast
            }
            else -> {}
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
