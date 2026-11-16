package dev.sayed.mehrabalmomen.presentation.screen.reminders

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReminderSettingsViewModel(
    private val settingsRepository: ReminderSettingsRepository,
    private val schedulerRepository: ReminderSchedulerRepository
) : BaseViewModel<ReminderUiState, Unit>(ReminderUiState()) {

    init {
        observeAndSyncReminders()
    }

    private fun observeAndSyncReminders() {
        viewModelScope.launch {
            settingsRepository.observeAllReminders().collectLatest { list ->
                updateState { it.copy(reminders = list) }
                schedulerRepository.rescheduleAll()
            }
        }
    }

    fun onToggleReminder(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateReminderEnabled(id, isEnabled)
            schedulerRepository.rescheduleAll()
        }
    }

    fun onDeleteReminder(id: Long) {
        viewModelScope.launch {
            settingsRepository.deleteReminder(id)
            schedulerRepository.rescheduleAll()
        }
    }

    fun onOpenAddBottomSheet() {
        updateState {
            it.copy(
                isBottomSheetOpen = true,
                editingReminderId = null,
                isEditingPrePrayer = false,
                reminderTitle = "",
                reminderDescription = "",
                reminderHour = 8,
                reminderMinute = 0,
                reminderRecurrence = RecurrencePattern.DAILY,
                isTimePickerOpen = false
            )
        }
    }

    fun onOpenEditBottomSheet(reminder: ReminderConfig) {
        updateState {
            it.copy(
                isBottomSheetOpen = true,
                editingReminderId = reminder.id,
                isEditingPrePrayer = reminder.type == ReminderType.BEFORE_PRAYER,
                reminderTitle = reminder.title,
                reminderDescription = reminder.description,
                reminderHour = reminder.hour,
                reminderMinute = reminder.minute,
                reminderRecurrence = reminder.recurrence,
                isTimePickerOpen = false
            )
        }
    }

    fun onCloseBottomSheet() {
        updateState {
            it.copy(
                isBottomSheetOpen = false,
                editingReminderId = null,
                isEditingPrePrayer = false
            )
        }
    }

    fun onTitleChanged(title: String) {
        updateState { it.copy(reminderTitle = title) }
    }

    fun onDescriptionChanged(description: String) {
        updateState { it.copy(reminderDescription = description) }
    }

    fun onOpenTimePicker() {
        updateState { it.copy(isTimePickerOpen = true) }
    }

    fun onCloseTimePicker() {
        updateState { it.copy(isTimePickerOpen = false) }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        updateState {
            it.copy(
                reminderHour = hour,
                reminderMinute = minute,
                isTimePickerOpen = false
            )
        }
    }

    fun onRecurrenceSelected(recurrence: RecurrencePattern) {
        updateState { it.copy(reminderRecurrence = recurrence) }
    }

    fun onPrePrayerDurationSelected(minutes: Int) {
        updateState { it.copy(reminderMinute = minutes) }
    }

    fun onSaveReminder() {
        val state = screenState.value
        viewModelScope.launch {
            val reminder = ReminderConfig(
                id = state.editingReminderId ?: 0L,
                title = state.reminderTitle,
                description = state.reminderDescription,
                hour = state.reminderHour,
                minute = state.reminderMinute,
                recurrence = state.reminderRecurrence,
                isEnabled = true,
                type = if (state.isEditingPrePrayer) ReminderType.BEFORE_PRAYER else null
            )
            settingsRepository.saveReminder(reminder)
            schedulerRepository.rescheduleAll()
            onCloseBottomSheet()
        }
    }
}
