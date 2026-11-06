package dev.sayed.mehrabalmomen.presentation.screen.reminders

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

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
        val now = Calendar.getInstance()
        updateState {
            it.copy(
                isBottomSheetOpen = true,
                editingReminderId = null,
                reminderTitle = "",
                reminderDescription = "",
                reminderHour = now.get(Calendar.HOUR_OF_DAY),
                reminderMinute = now.get(Calendar.MINUTE),
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
                editingReminderId = null
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

    fun onSaveReminder() {
        val current = screenState.value
        val title = current.reminderTitle.trim()
        if (title.isBlank()) return

        viewModelScope.launch {
            val existingReminder = current.editingReminderId?.let { id ->
                current.reminders.find { it.id == id }
            }

            val config = ReminderConfig(
                id = current.editingReminderId ?: 0L,
                title = title,
                description = current.reminderDescription.trim(),
                hour = current.reminderHour,
                minute = current.reminderMinute,
                recurrence = current.reminderRecurrence,
                isEnabled = existingReminder?.isEnabled ?: true,
                type = existingReminder?.type
            )

            settingsRepository.saveReminder(config)
            schedulerRepository.rescheduleAll()
            updateState {
                it.copy(
                    isBottomSheetOpen = false,
                    editingReminderId = null
                )
            }
        }
    }
}
