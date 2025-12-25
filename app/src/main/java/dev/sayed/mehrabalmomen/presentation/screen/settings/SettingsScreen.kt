package dev.sayed.mehrabalmomen.presentation.screen.settings

import SettingsUiState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.SingleSelectionDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val state by settingsViewModel.screenState.collectAsState()

    state.dialog?.let { dialog ->
        SingleSelectionDialog(
            title = dialog.titleRes,
            items = dialog.options,
            selectedIndex = dialog.selectedIndex,
            onConfirm = { index ->
                settingsViewModel.onDialogConfirm(index)

                if (dialog.type == SettingsUiState.SelectionDialogType.LANGUAGE) {


                }
                if (dialog.type == SettingsUiState.SelectionDialogType.THEME) {
                    settingsViewModel.onThemeSelected(SettingsUiState.ThemeState.entries[index])
                }
                if (dialog.type == SettingsUiState.SelectionDialogType.MADHAB) {
                    settingsViewModel.onMadhabSelected(SettingsUiState.MadhabState.entries[index])
                }
            },
            onDismiss = { settingsViewModel.onDialogDismiss() }
        )
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars),
        contentPadding = PaddingValues(
            top = 24.dp,
            start = 16.dp,
            end = 16.dp
        ),
        columns = GridCells.Adaptive(320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            AppBar(
                onBackClick = { navController.popBackStack() },
                title = "Settings"
            )
        }
        state.sections.forEach { section ->

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = localizedString(section.titleRes),
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.primary.primary
                )
            }

            items(section.items) {
                SettingsItem(it, listener = settingsViewModel)
            }
        }
    }
}

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
                    SettingsUiState.SettingsAction.MADHAB -> listener.onItemClick(item.action)

                    SettingsUiState.SettingsAction.LOCATION -> listener.onLocationClick()
                    SettingsUiState.SettingsAction.CALCULATION_METHOD -> listener.onCalculationMethodClick()
                    SettingsUiState.SettingsAction.HELP_FEEDBACK -> listener.onHelpFeedbackClick()
                    SettingsUiState.SettingsAction.RATE_APP -> listener.onRateAppClick()
                    SettingsUiState.SettingsAction.ABOUT -> listener.onAboutClick()
                }
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.color.primary.primary)
                .padding(4.dp), contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = Theme.color.primary.onPrimary
            )
        }

        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            text = localizedString(item.title),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.label.medium
        )
        AnimatedVisibility(visible = item.description != 0) {
            Text(
                text = localizedString(item.description),
                color = Color(0xFF818599),
                style = Theme.textStyle.label.small
            )
        }

    }
}
