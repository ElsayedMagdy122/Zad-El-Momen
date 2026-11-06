package dev.sayed.mehrabalmomen.presentation.screen.reminders

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.presentation.base.UiIcon
import dev.sayed.mehrabalmomen.presentation.base.UiText

fun getReminderTitle(config: ReminderConfig): UiText {
    return if (config.type != null) {
        when (config.type) {
            ReminderType.MORNING_AZKAR -> UiText.StringResource(R.string.azkar_morning)
            ReminderType.EVENING_AZKAR -> UiText.StringResource(R.string.azkar_evening)
            ReminderType.FRIDAY_SUNNAN -> UiText.StringResource(R.string.friday_sunnah)
            ReminderType.DAILY_WORD -> UiText.StringResource(R.string.daily_quran_word)
            ReminderType.BEFORE_PRAYER -> UiText.DynamicString("التنبيه قبل الصلاة")
        }
    } else {
        UiText.DynamicString(config.title)
    }
}

fun getReminderTitle(type: ReminderType): UiText = when (type) {
    ReminderType.MORNING_AZKAR -> UiText.StringResource(R.string.azkar_morning)
    ReminderType.EVENING_AZKAR -> UiText.StringResource(R.string.azkar_evening)
    ReminderType.FRIDAY_SUNNAN -> UiText.StringResource(R.string.friday_sunnah)
    ReminderType.DAILY_WORD -> UiText.StringResource(R.string.daily_quran_word)
    ReminderType.BEFORE_PRAYER -> UiText.DynamicString("التنبيه قبل الصلاة")
}

fun getReminderIcon(config: ReminderConfig): UiIcon = when (config.type) {
    ReminderType.MORNING_AZKAR -> UiIcon(R.drawable.ic_morning)
    ReminderType.EVENING_AZKAR -> UiIcon(R.drawable.ic_sleep)
    ReminderType.FRIDAY_SUNNAN -> UiIcon(R.drawable.mosque_02)
    ReminderType.DAILY_WORD -> UiIcon(R.drawable.ic_quran_02)
    ReminderType.BEFORE_PRAYER -> UiIcon(R.drawable.ic_accurate_prayer)
    null -> UiIcon(R.drawable.ic_notifications)
}

fun getRecurrenceText(recurrence: RecurrencePattern): String = when (recurrence) {
    RecurrencePattern.DAILY -> "كل يوم"
    RecurrencePattern.WEEKLY_THURSDAY -> "كل خميس"
    RecurrencePattern.WEEKLY -> "كل أسبوع"
    RecurrencePattern.ONCE -> "مرة واحدة"
}
