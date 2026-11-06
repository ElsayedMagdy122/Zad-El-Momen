package dev.sayed.mehrabalmomen.presentation.screen.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.StateMessage
import dev.sayed.mehrabalmomen.presentation.screen.reminders.components.AddReminderBottomSheet
import dev.sayed.mehrabalmomen.presentation.screen.reminders.components.RecurrencePickerDialog
import dev.sayed.mehrabalmomen.presentation.screen.reminders.components.ReminderItem
import dev.sayed.mehrabalmomen.presentation.screen.reminders.components.RemoveReminderDialog
import dev.sayed.mehrabalmomen.presentation.screen.reminders.components.TimePickerDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReminderSettingsScreen(
    navController: NavController,
    viewModel: ReminderSettingsViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    var pendingDeleteItem by remember { mutableStateOf<ReminderConfig?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        containerColor = Theme.color.surfaces.surface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onOpenAddBottomSheet,
                containerColor = Theme.color.primary.primary,
                contentColor = Theme.color.primary.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة تنبيه",
                    tint = Theme.color.primary.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.color.surfaces.surface)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                AppBar(
                    isBackEnabled = true,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    title = localizedString(R.string.notifications)
                )

                if (state.reminders.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
                    ) {
                        items(
                            items = state.reminders,
                            key = { it.id }
                        ) { reminder ->
                            val canDelete = reminder.type != ReminderType.BEFORE_PRAYER
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    if (canDelete && dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                        pendingDeleteItem = reminder
                                        false
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                enableDismissFromEndToStart = canDelete,
                                backgroundContent = {
                                    if (canDelete) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(vertical = 6.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(
                                                    if (Theme.isDark) Theme.color.semantic.error.copy(alpha = 0.2f)
                                                    else Color(0xFFFEE4E2)
                                                ),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_trash),
                                                contentDescription = "حذف التنبيه",
                                                tint = Theme.color.semantic.error,
                                                modifier = Modifier
                                                    .padding(end = 16.dp)
                                                    .size(22.dp)
                                            )
                                        }
                                    }
                                }
                            ) {
                                ReminderItem(
                                    config = reminder,
                                    onToggle = { isChecked ->
                                        viewModel.onToggleReminder(reminder.id, isChecked)
                                    },
                                    onEditClick = {
                                        viewModel.onOpenEditBottomSheet(reminder)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (state.reminders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    StateMessage(
                        imageRes = R.drawable.no_data_yet,
                        imageModifier = Modifier.size(110.dp),
                        title = "ابدأ بتذكير يقرّبك",
                        subtitle = "لم تضف أي تذكير بعد. اختر وقتًا لأذكارك أو وردك القرآني، ودعنا نذكّرك كل يوم."
                    )
                }
            }
        }
    }

    // Delete confirmation dialog matching BookmarkScreen style
    pendingDeleteItem?.let { selectedItem ->
        RemoveReminderDialog(
            reminderTitle = selectedItem.title,
            onDismiss = {
                pendingDeleteItem = null
            },
            onConfirm = {
                viewModel.onDeleteReminder(selectedItem.id)
                pendingDeleteItem = null
            }
        )
    }

    // Add / Edit reminder Bottom Sheet
    if (state.isBottomSheetOpen) {
        AddReminderBottomSheet(
            title = state.reminderTitle,
            onTitleChange = viewModel::onTitleChanged,
            description = state.reminderDescription,
            onDescriptionChange = viewModel::onDescriptionChanged,
            hour = state.reminderHour,
            minute = state.reminderMinute,
            onTimeClick = viewModel::onOpenTimePicker,
            selectedRecurrence = state.reminderRecurrence,
            onRecurrenceSelect = viewModel::onRecurrenceSelected,
            onSaveClick = viewModel::onSaveReminder,
            onDismiss = viewModel::onCloseBottomSheet,
            isEditMode = state.editingReminderId != null,
            isPrePrayerMode = state.isEditingPrePrayer,
            onMinuteChange = viewModel::onPrePrayerDurationSelected
        )
    }

    // Time picker for Bottom Sheet
    if (state.isTimePickerOpen) {
        TimePickerDialog(
            initialHour = state.reminderHour,
            initialMinute = state.reminderMinute,
            onDismiss = viewModel::onCloseTimePicker,
            onConfirm = { hour, minute ->
                viewModel.onTimeSelected(hour, minute)
            }
        )
    }
}
