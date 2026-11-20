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
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.CheckboxItem
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.confirm
import zadelmomen.presentation.generated.resources.ic_pause
import zadelmomen.presentation.generated.resources.ic_play
import zadelmomen.presentation.generated.resources.ic_prayer_times_selected
import zadelmomen.presentation.generated.resources.prayer_times_widget
import zadelmomen.presentation.generated.resources.settings
import zadelmomen.presentation.generated.resources.v
import zadelmomen.presentation.generated.resources.version
import zadelmomen.presentation.generated.resources.widget_pin_manual_guide
import dev.sayed.mehrabalmomen.design_system.base.UiText as DesignUiText

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val state by settingsViewModel.screenState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    var toast by remember { mutableStateOf<ToastDetails?>(null) }
    val versionNumber = "1.0.0"

    CollectEffect(settingsViewModel.effect) { effect ->
        when (effect) {
            SettingsEffect.NavigateToHelpFeedback -> {
                navController.navigate(Route.ReportBugScreen)
            }

            SettingsEffect.NavigateToLocation -> {
                navController.navigate(Route.MapsScreen)
            }

            SettingsEffect.NavigateToRateApp -> {
                runCatching {
                    uriHandler.openUri("https://play.google.com/store/apps/details?id=dev.sayed.mehrabalmomen")
                }
            }

            is SettingsEffect.LaunchDonation -> {
                // Donation handled via ViewModel
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
                toast = ToastDetails(
                    title = DesignUiText.ResString(Res.string.prayer_times_widget),
                    message = DesignUiText.ResString(Res.string.widget_pin_manual_guide),
                    icon = Res.drawable.ic_prayer_times_selected
                )
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
            items = dialog.options,
            title = dialog.title,
            description = dialog.description ?: UiText.DynamicString(""),
            selectedIndex = dialog.selectedIndex,
            onConfirm = { index ->
                settingsViewModel.onDialogConfirm(index)
            },
            onDismiss = {
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
    items: List<SelectionItem>,
    title: UiText,
    description: UiText,
    selectedIndex: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
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
            SingleSelectionContent(
                items = items,
                selectedIndex = currentSelected,
                onItemSelected = { index -> currentSelected = index }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = localizedString(Res.string.confirm),
                onClick = {
                    onConfirm(currentSelected)
                    onDismiss()
                }
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
    val icon: DrawableResource? = null
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
            modifier = Modifier
                .padding(horizontal = 8.dp),
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
