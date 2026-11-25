package dev.sayed.mehrabalmomen.presentation.screen.prayers

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.AlarmPermissionsBottomSheet
import dev.sayed.mehrabalmomen.presentation.components.AppBarAction
import dev.sayed.mehrabalmomen.presentation.components.QuranAppBar
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.BatteryOptimizationBottomSheet
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.MoazenSelectionBottomSheet
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.NextPrayerCard
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.PrayerItem
import dev.sayed.mehrabalmomen.presentation.screen.settings.SelectionItem
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsUiState
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.ic_moazen
import zadelmomen.presentation.generated.resources.ic_setting_error_03
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
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        viewModel.onScreenOpened()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshBatteryStatus()
                viewModel.onScreenOpened()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            PrayerTimesEffect.NavigateBack -> {
                navController.popBackStack()
            }
            PrayerTimesEffect.RequestNotificationPermission -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            PrayerTimesEffect.RequestExactAlarm -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            }
            PrayerTimesEffect.RequestIgnoreBatteryOptimization -> {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {
                    val fallback = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try {
                        context.startActivity(fallback)
                    } catch (_: Exception) {}
                }
            }
            PrayerTimesEffect.RequestOverlayPermission -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            }
            PrayerTimesEffect.OpenAppSettings -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
            else -> {}
        }
    }

    var isVisible by remember { mutableStateOf(false) }
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
                        ),
                        AppBarAction(
                            icon = painterResource(Res.drawable.ic_setting_error_03),
                            onClick = viewModel::onBatteryWarningClick
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

    if (state.showBatteryDialog) {
        BatteryOptimizationBottomSheet(
            isBatteryOptimizationEnabled = state.isBatteryOptimizationEnabled,
            isOverlayPermissionGranted = state.isOverlayPermissionGranted,
            batteryInstructions = state.batteryInstructions,
            onOpenBatterySettings = {
                viewModel.onOpenSettingsClicked()
            },
            onOpenOverlaySettings = {
                viewModel.onOpenOverlaySettingsClicked()
            },
            onDismiss = viewModel::onDismissBatteryDialog
        )
    }

    if (state.showAlarmPermissionBottomSheet) {
        AlarmPermissionsBottomSheet(
            isNotificationPermissionMissing = state.isNotificationPermissionMissing,
            isExactAlarmPermissionMissing = state.isExactAlarmPermissionMissing,
            isBatteryOptimizationMissing = state.isBatteryOptimizationMissing,
            onGrantNotification = viewModel::onGrantNotificationPermission,
            onGrantExactAlarm = viewModel::onGrantExactAlarmPermission,
            onGrantBattery = viewModel::onGrantBatteryPermission,
            onGrantAll = viewModel::onGrantAlarmPermissions,
            onIgnore = viewModel::onIgnoreAlarmPermissions
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
