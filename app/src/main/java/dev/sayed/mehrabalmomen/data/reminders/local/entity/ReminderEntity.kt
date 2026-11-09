package dev.sayed.mehrabalmomen.data.reminders.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val hour: Int,
    val minute: Int,
    val recurrence: String = "DAILY", // "DAILY", "WEEKLY_THURSDAY", "WEEKLY", "ONCE"
    val isEnabled: Boolean = true,
    val reminderType: String? = null // ReminderType enum name if default, null if custom
)
