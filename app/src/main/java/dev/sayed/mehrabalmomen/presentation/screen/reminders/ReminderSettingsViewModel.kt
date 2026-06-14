package dev.sayed.mehrabalmomen.presentation.screen.reminders

import androidx.lifecycle.viewModelScope
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

    fun onToggleReminder(type: ReminderType, isEnabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsRepository.saveReminderConfig(type, isEnabled, hour, minute)
        }
    }

    fun onTimeSelected(type: ReminderType, isEnabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsRepository.saveReminderConfig(type, isEnabled, hour, minute)
        }
    }
}