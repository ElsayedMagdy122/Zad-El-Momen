package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.NextPrayerCard
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.PrayerItem
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.prayer_times

@Composable
actual fun PrayerTimesScreen(
    navController: NavController,
    viewModel: PrayerTimesViewModel
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val countdownTime by viewModel.countdownTime.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            PrayerTimesEffect.NavigateBack -> {
                navController.popBackStack()
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onScreenOpened()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        item {
            AppBar(
                isBackEnabled = false,
                onBackClick = viewModel::onClickBack,
                title = localizedString(Res.string.prayer_times),
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
        }
        item {
            NextPrayerCard(
                state = state,
                countdownTime = countdownTime,
            )
        }
        items(state.prayers) { prayerUiState ->
            PrayerItem(
                prayerName = prayerUiState.prayerName,
                prayerLabel = prayerUiState.name,
                prayerTime = prayerUiState.time.time,
                isAm = prayerUiState.time.isAm,
                isNextPrayer = prayerUiState.isUpComing,
                isNotificationEnabled = prayerUiState.isNotificationEnabled,
                onNotificationClick = { name, enabled ->
                    viewModel.onClickEnablePrayer(name, enabled)
                }
            )
        }
    }
}
