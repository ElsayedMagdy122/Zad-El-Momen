package dev.sayed.mehrabalmomen.domain.model

data class ReminderConfig(
    val type: ReminderType,
    val isEnabled: Boolean,
    val hour: Int,
    val minute: Int
)