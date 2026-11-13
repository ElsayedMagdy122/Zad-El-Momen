package dev.sayed.mehrabalmomen.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.Switch
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.domain.billing.SupportTiers
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.CheckboxItem
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import dev.sayed.mehrabalmomen.presentation.utils.getAppVersionName
import dev.sayed.mehrabalmomen.presentation.utils.openUrl
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val state by settingsViewModel.screenState.collectAsStateWithLifecycle()
    var toast by remember { mutableStateOf<ToastDetails?>(null) }
    val uriHandler = LocalUriHandler.current
    val versionNumber = getAppVersionName()

    CollectEffect(settingsViewModel.effect) { effect ->
        when (effect) {
            SettingsEffect.NavigateToHelpFeedback -> {
                navController.navigate(Route.ReportBugScreen)
            }

            SettingsEffect.NavigateToLocation -> {
                navController.navigate(Route.MapsScreen)
            }

            SettingsEffect.NavigateToRateApp -> {
                openUrl("market://details?id=dev.sayed.mehrabalmomen", uriHandler)
            }

            is SettingsEffect.LaunchDonation -> {
                settingsViewModel.launchDonationFlow(Unit, effect.productId)
            }

            is SettingsEffect.ShowToast -> {
                toast = effect.toast
            }

            SettingsEffect.NavigateToContactUs -> {
                navController.navigate(Route.ContactUsScreen)
            }

            SettingsEffect.NavigateToPrivacy -> {
                navController.navigate(Route.PrivacyScreen)
            }

            SettingsEffect.NavigateToNotifications -> {
                navController.navigate(Route.ReminderSettingsScreen)
            }

            SettingsEffect.NavigateToFAQ -> {
                navController.navigate(Route.FAQScreen)
            }

            SettingsEffect.RequestPinWidget -> {
                settingsViewModel.requestPinWidget {
                    toast = ToastDetails(
                        title = Res.string.prayer_times_widget,
                        message = Res.string.widget_pin_manual_guide,
                        icon = Res.drawable.ic_prayer_times_selected
                    )
                }
            }
        }
    }

    LaunchedEffect(toast) {
        toast?.let {
            val current = it
            delay(3000)
            if (toast == current) toast = null
        }
    }

    state.dialog?.let { dialog ->
        SettingsBottomSheet(
            settingsViewModel = settingsViewModel,
            isMoazen = dialog.type == SettingsUiState.SelectionDialogType.MOAZEN,
            items = dialog.options,
            title = dialog.title,
            description = dialog.description ?: UiText.DynamicString(""),
            selectedIndex = dialog.selectedIndex,
            onConfirm = { index ->
                settingsViewModel.stopPreview()
                settingsViewModel.onDialogConfirm(index)
            },
            onDismiss = {
                settingsViewModel.stopPreview()
                settingsViewModel.onDialogDismiss()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        LazyVerticalGrid(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            columns = GridCells.Adaptive(320.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                AppBar(
                    isBackEnabled = false,
                    onBackClick = { navController.popBackStack() },
                    title = localizedString(Res.string.settings)
                )
            }
            state.sections.forEach { section ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = section.title.asString(),
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.primary.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                items(section.items) {
                    SettingsItem(it, listener = settingsViewModel)
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(
                        Res.string.v,
                        localizedString(Res.string.version),
                        versionNumber
                    ),
                    style = Theme.textStyle.label.small,
                    color = Theme.color.secondary.shadeSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        toast?.let {
            PrimaryToast(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp),
                data = it,
                isSuccess = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    settingsViewModel: SettingsViewModel? = null,
    items: List<SelectionItem>,
    title: UiText,
    description: UiText,
    selectedIndex: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
    isMoazen: Boolean = false,
) {
    var currentSelected by remember { mutableStateOf(selectedIndex) }

    BottomSheetDs(onDismiss = onDismiss) {
        Text(
            text = title.asString(),
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary
        )
        Text(
            text = description.asString(),
            style = Theme.textStyle.label.medium,
            color = Theme.color.semantic.shadeTertiary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isMoazen && settingsViewModel != null) {
                SingleSelectionContentWithPreview(
                    items = items,
                    selectedIndex = currentSelected,
                    onItemSelected = { index -> currentSelected = index },
                    playSound = { index -> settingsViewModel.playPreview(index) },
                    stopSound = { settingsViewModel.stopPreview() }
                )
            } else {
                SingleSelectionContent(
                    items = items,
                    selectedIndex = currentSelected,
                    onItemSelected = { index -> currentSelected = index }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                text = localizedString(Res.string.confirm),
                onClick = {
                    onConfirm(currentSelected)
                    onDismiss()
                }
            )
        }
    }
}

fun getSupportName(id: String): StringResource {
    return when (id) {
        SupportTiers.TIER_1 -> Res.string.support_1
        SupportTiers.TIER_5 -> Res.string.support_2
        SupportTiers.TIER_10 -> Res.string.support_3
        SupportTiers.TIER_25 -> Res.string.support_4
        SupportTiers.TIER_100 -> Res.string.support_5
        else -> Res.string.support_1
    }
}

@Composable
fun SingleSelectionContentWithPreview(
    items: List<SelectionItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    playSound: (Int) -> Unit,
    stopSound: () -> Unit
) {
    var currentlyPlayingIndex by remember { mutableStateOf<Int?>(null) }

    items.forEachIndexed { index, item ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.color.surfaces.surfaceLow)
                .clickable { onItemSelected(index) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Theme.color.surfaces.surfaceHigh)
                    .clickable {
                        if (currentlyPlayingIndex == index) {
                            stopSound()
                            currentlyPlayingIndex = null
                        } else {
                            stopSound()
                            playSound(index)
                            currentlyPlayingIndex = index
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        if (currentlyPlayingIndex == index) Res.drawable.ic_pause else Res.drawable.ic_play
                    ),
                    contentDescription = null,
                    tint = Theme.color.primary.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            CheckboxItem(
                modifier = Modifier.weight(1f),
                text = item.text?.asString() ?: "",
                description = item.description,
                icon = item.icon?.let { painterResource(it) },
                isChecked = selectedIndex == index,
                onCheckedChange = { checked ->
                    if (checked) onItemSelected(index)
                },
                backgroundColor = Color.Transparent
            )
        }
    }
}

@Composable
fun SingleSelectionContent(
    items: List<SelectionItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    items.forEachIndexed { index, item ->
        CheckboxItem(
            text = item.text?.asString() ?: "",
            description = item.description,
            icon = item.icon?.let { painterResource(it) },
            isChecked = selectedIndex == index,
            onCheckedChange = { checked ->
                if (checked) onItemSelected(index)
            },
            backgroundColor = Theme.color.surfaces.surfaceLow
        )
    }
}

data class SelectionItem(
    val text: UiText? = null,
    val description: String? = null,
    val icon: DrawableResource? = null,
    val resId: Any? = null
)

@Composable
fun SettingsItem(
    item: SettingsUiState.SettingsItemUiState,
    listener: SettingsInteractionListener,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .clickable {
                when (item.action) {
                    SettingsUiState.SettingsAction.LANGUAGE,
                    SettingsUiState.SettingsAction.THEME,
                    SettingsUiState.SettingsAction.MADHAB,
                    SettingsUiState.SettingsAction.CALCULATION_METHOD,
                    SettingsUiState.SettingsAction.MOAZEN,
                    SettingsUiState.SettingsAction.TEXT_FONT,
                    SettingsUiState.SettingsAction.TAFSEER -> listener.onItemClick(item.action)

                    SettingsUiState.SettingsAction.COMPANION -> listener.onItemClick(item.action)
                    SettingsUiState.SettingsAction.LOCATION -> listener.onLocationClick()
                    SettingsUiState.SettingsAction.HELP_FEEDBACK -> listener.onHelpFeedbackClick()
                    SettingsUiState.SettingsAction.RATE_APP -> listener.onRateAppClick()
                    SettingsUiState.SettingsAction.ABOUT -> listener.onAboutClick()
                    SettingsUiState.SettingsAction.PRIVACY_POLICY -> listener.onPrivacyAndPolicy()
                    SettingsUiState.SettingsAction.CONTACT_US -> listener.onContactUs()
                    SettingsUiState.SettingsAction.NOTIFICATIONS -> listener.onNotifications()
                    SettingsUiState.SettingsAction.FAQ -> listener.onItemClick(item.action)
                    SettingsUiState.SettingsAction.WIDGET -> listener.onItemClick(item.action)
                }
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(item.icon),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )

        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = item.title.asString(),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.label.medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        if (item.value != null) {
            Switch(
                isChecked = item.value,
                onCheckedChange = { listener.onItemClick(item.action) }
            )
        } else if (item.description != null) {
            Text(
                text = item.description.asString(),
                color = Theme.color.semantic.shadeTertiary,
                style = Theme.textStyle.label.small,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
        }
    }
}
