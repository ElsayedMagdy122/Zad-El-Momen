package dev.sayed.mehrabalmomen.domain.model

enum class ReminderType(
    val alarmId: Int,
    val defaultHour: Int,
    val defaultMinute: Int,
    val recurrence: RecurrencePattern,
    val preferenceKey: String
) {
    MORNING_AZKAR(101, 7, 0, RecurrencePattern.DAILY, "morning_azkar"),
    EVENING_AZKAR(102, 16, 30, RecurrencePattern.DAILY, "evening_azkar"),
    FRIDAY_SUNNAN(
        103,
        7,
        0,
        RecurrencePattern.WEEKLY_THURSDAY,
        "friday_sunnan"
    ),
    DAILY_WORD(104, 5, 0, RecurrencePattern.DAILY, "daily_word")
}
