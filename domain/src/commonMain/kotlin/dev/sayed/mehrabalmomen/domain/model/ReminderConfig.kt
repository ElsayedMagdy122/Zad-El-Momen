package dev.sayed.mehrabalmomen.domain.model

data class ReminderConfig(
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val hour: Int,
    val minute: Int,
    val recurrence: RecurrencePattern = RecurrencePattern.DAILY,
    val isEnabled: Boolean = true,
    val type: ReminderType? = null
)