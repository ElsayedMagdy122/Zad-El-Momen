package dev.sayed.mehrabalmomen.presentation.screen.reminders

import dev.sayed.mehrabalmomen.domain.model.ReminderConfig

data class ReminderUiState(val reminders: List<ReminderConfig> = emptyList())

