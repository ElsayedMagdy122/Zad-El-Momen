package dev.sayed.mehrabalmomen.presentation.screen.settings

import SettingsUiState
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.play.core.review.ReviewManagerFactory
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.CheckboxItem
import dev.sayed.mehrabalmomen.presentation.components.SelectionItem
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val state by settingsViewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    CollectEffect(settingsViewModel.effect) { effect ->
        when (effect) {
            SettingsEffect.NavigateToAbout -> TODO()
            SettingsEffect.NavigateToHelpFeedback -> TODO()
            SettingsEffect.NavigateToLocation -> {
                navController.navigate(Route.MapsScreen)
            }

            SettingsEffect.NavigateToRateApp -> {
                openStoreReview(context)
            }


        }
    }
    state.dialog?.let { dialog ->
        SettingsBottomSheet(
            items = dialog.options,
            title = localizedString(dialog.titleRes),
            description = dialog.descriptionRes?.let { localizedString(it) } ?: "",
            selectedIndex = dialog.selectedIndex,
            onConfirm = { index ->
                settingsViewModel.onDialogConfirm(index)

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
                title = localizedString(R.string.settings)
            )
        }
        state.sections.forEach { section ->

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = localizedString(section.titleRes),
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.primary.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            items(section.items) {
                SettingsItem(it, listener = settingsViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    items: List<SelectionItem>,
    title: String,
    description: String,
    selectedIndex: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var currentSelected by remember { mutableStateOf(selectedIndex) }
    BottomSheetDs(onDismiss = onDismiss) {
        Text(
            text = title,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary
        )
        Text(
            text = description,
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
                onItemSelected = { index ->
                    currentSelected = index
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = localizedString(R.string.confirm),
                onClick = {
                    onConfirm(currentSelected)
                    onDismiss()
                })
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
            text = localizedString(item.text),
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
                    SettingsUiState.SettingsAction.CALCULATION_METHOD -> listener.onItemClick(
                        item.action
                    )

                    SettingsUiState.SettingsAction.LOCATION -> listener.onLocationClick()
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
                .padding(horizontal = 8.dp),
            text = localizedString(item.title),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.label.medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (
            item.description != 0 || item.descriptionText != null
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = when {
                    item.description != 0 ->
                        localizedString(item.description)

                    item.descriptionText != null ->
                        item.descriptionText

                    else -> ""
                },
                color = Color(0xFF818599),
                style = Theme.textStyle.label.small,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
        }

    }
}

fun launchReview(context: Context) {
    val activity = context as? Activity ?: return

    val reviewManager = ReviewManagerFactory.create(context)
    val request = reviewManager.requestReviewFlow()

    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            reviewManager.launchReviewFlow(activity, task.result)
        }
    }
}

fun openStoreReview(context: Context) {
    val uri = Uri.parse("market://details?id=${context.packageName}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}