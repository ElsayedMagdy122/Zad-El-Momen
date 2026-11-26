package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.AppBarAction
import dev.sayed.mehrabalmomen.presentation.components.QuranAppBar
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.MoazenSelectionBottomSheet
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.NextPrayerCard
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.PrayerItem
import dev.sayed.mehrabalmomen.presentation.screen.settings.SelectionItem
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsUiState
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.ic_moazen
import zadelmomen.presentation.generated.resources.mozan_abed_albaset
import zadelmomen.presentation.generated.resources.mozan_makkah
import zadelmomen.presentation.generated.resources.mozan_mansoor_alzahrani
import zadelmomen.presentation.generated.resources.mozan_mishary_alafasi
import zadelmomen.presentation.generated.resources.mozan_mohammed_almenshawy
import zadelmomen.presentation.generated.resources.mozan_nasser_alqatami
import zadelmomen.presentation.generated.resources.mozan_suhaib_khatba
import zadelmomen.presentation.generated.resources.prayer_times

@Composable
actual fun PrayerTimesScreen(
    navController: NavController,
    viewModel: PrayerTimesViewModel
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val countdownTime by viewModel.countdownTime.collectAsStateWithLifecycle()
    var isVisible by remember { mutableStateOf(false) }

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
        isVisible = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        item {
            StaggeredPrayerItem(index = 0, isVisible = isVisible) {
                QuranAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(Theme.color.surfaces.surface),
                    title = localizedString(Res.string.prayer_times),
                    onBackClick = viewModel::onClickBack,
                    titleColor = Theme.color.primary.shadePrimary,
                    actions = listOf(
                        AppBarAction(
                            icon = painterResource(Res.drawable.ic_moazen),
                            onClick = viewModel::openMoazenDialog
                        )
                    )
                )
            }
        }
        item {
            StaggeredPrayerItem(index = 1, isVisible = isVisible) {
                NextPrayerCard(
                    state = state,
                    countdownTime = countdownTime,
                )
            }
        }
        itemsIndexed(state.prayers) { index, prayerUiState ->
            StaggeredPrayerItem(index = index + 2, isVisible = isVisible) {
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

    if (state.showMoazenDialog) {
        val moazenOptions = SettingsUiState.Moazen.entries.map { moazen ->
            SelectionItem(
                text = UiText.StringRes(
                    when (moazen) {
                        SettingsUiState.Moazen.AZAN_ABED_ALBASET -> Res.string.mozan_abed_albaset
                        SettingsUiState.Moazen.AZAN_MAKKAH -> Res.string.mozan_makkah
                        SettingsUiState.Moazen.AZAN_MANSOOR_AL_ZAHRANI -> Res.string.mozan_mansoor_alzahrani
                        SettingsUiState.Moazen.AZAN_MISHARY_ALAFASI -> Res.string.mozan_mishary_alafasi
                        SettingsUiState.Moazen.AZAN_MOHAMMED_ALMENSHWY -> Res.string.mozan_mohammed_almenshawy
                        SettingsUiState.Moazen.AZAN_NASSER_ALQATAMI -> Res.string.mozan_nasser_alqatami
                        SettingsUiState.Moazen.AZAN_SUHAIB_KHATBA -> Res.string.mozan_suhaib_khatba
                    }
                )
            )
        }

        MoazenSelectionBottomSheet(
            items = moazenOptions,
            selectedIndex = SettingsUiState.Moazen.entries.indexOf(state.selectedMoazen),
            onConfirm = viewModel::onConfirmMoazen,
            onDismiss = viewModel::dismissMoazenDialog,
            playSound = viewModel::playPreview,
            stopSound = viewModel::stopPreview
        )
    }
}

@Composable
private fun StaggeredPrayerItem(
    index: Int,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(index * 70L)
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 420,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Box(
        modifier = modifier.graphicsLayer {
            alpha = animatedProgress.value
            translationY = (1f - animatedProgress.value) * 45.dp.toPx()
        }
    ) {
        content()
    }
}
