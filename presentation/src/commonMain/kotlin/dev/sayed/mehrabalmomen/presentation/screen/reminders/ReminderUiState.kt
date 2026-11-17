package dev.sayed.mehrabalmomen.presentation.screen.reminders

import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig

data class ReminderUiState(
    val reminders: List<ReminderConfig> = emptyList(),
    val isBottomSheetOpen: Boolean = false,
    val editingReminderId: Long? = null,
    val isEditingPrePrayer: Boolean = false,
    val reminderTitle: String = "",
    val reminderDescription: String = "",
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0,
    val reminderRecurrence: RecurrencePattern = RecurrencePattern.DAILY,
    val isTimePickerOpen: Boolean = false,
    val selectedReminderForTimeEdit: ReminderConfig? = null,
    val selectedReminderForRecurrenceEdit: ReminderConfig? = null
)
