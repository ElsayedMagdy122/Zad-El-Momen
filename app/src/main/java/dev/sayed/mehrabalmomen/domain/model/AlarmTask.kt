package dev.sayed.mehrabalmomen.domain.model

/**
 * Represents the different types of background tasks that can be triggered by an alarm.
 */
sealed class AlarmTask {
    /** Triggers the Azan/Prayer notification service. */
    data class Prayer(val prayerName: String, val prayerTimeMillis: Long = 0L) : AlarmTask()

    /** Triggers a background refresh of prayer times (usually at midnight). */
    object DailyRefresh : AlarmTask()

    /** Triggers a general reminder notification (e.g., Azkar or custom). */
    data class Reminder(
        val reminderId: Long = 0L,
        val title: String = "",
        val body: String = "",
        val typeName: String? = null
    ) : AlarmTask()
}
