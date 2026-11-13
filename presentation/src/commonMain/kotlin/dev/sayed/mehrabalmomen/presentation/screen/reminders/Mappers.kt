package dev.sayed.mehrabalmomen.presentation.screen.reminders

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.azkar_morning
import dev.sayed.mehrabalmomen.designsystem.resources.azkar_evening
import dev.sayed.mehrabalmomen.designsystem.resources.friday_sunnah
import dev.sayed.mehrabalmomen.designsystem.resources.daily_quran_word
import dev.sayed.mehrabalmomen.designsystem.resources.ic_morning
import dev.sayed.mehrabalmomen.designsystem.resources.ic_sleep
import dev.sayed.mehrabalmomen.designsystem.resources.mosque_02
import dev.sayed.mehrabalmomen.designsystem.resources.ic_quran_02
import dev.sayed.mehrabalmomen.designsystem.resources.ic_accurate_prayer
import dev.sayed.mehrabalmomen.designsystem.resources.ic_alarm_clock
import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.presentation.base.UiIcon
import dev.sayed.mehrabalmomen.presentation.base.UiText

fun getReminderTitle(config: ReminderConfig): UiText {
    val type = config.type
    return if (type != null) {
        when (type) {
            ReminderType.MORNING_AZKAR -> UiText.Resource(Res.string.azkar_morning)
            ReminderType.EVENING_AZKAR -> UiText.Resource(Res.string.azkar_evening)
            ReminderType.FRIDAY_SUNNAN -> UiText.Resource(Res.string.friday_sunnah)
            ReminderType.DAILY_WORD -> UiText.Resource(Res.string.daily_quran_word)
            ReminderType.BEFORE_PRAYER -> UiText.DynamicString("التنبيه قبل الصلاة")
        }
    } else {
        UiText.DynamicString(config.title)
    }
}

fun getReminderTitle(type: ReminderType): UiText = when (type) {
    ReminderType.MORNING_AZKAR -> UiText.Resource(Res.string.azkar_morning)
    ReminderType.EVENING_AZKAR -> UiText.Resource(Res.string.azkar_evening)
    ReminderType.FRIDAY_SUNNAN -> UiText.Resource(Res.string.friday_sunnah)
    ReminderType.DAILY_WORD -> UiText.Resource(Res.string.daily_quran_word)
    ReminderType.BEFORE_PRAYER -> UiText.DynamicString("التنبيه قبل الصلاة")
}

fun getReminderIcon(config: ReminderConfig): UiIcon = when (config.type) {
    ReminderType.MORNING_AZKAR -> UiIcon(Res.drawable.ic_morning)
    ReminderType.EVENING_AZKAR -> UiIcon(Res.drawable.ic_sleep)
    ReminderType.FRIDAY_SUNNAN -> UiIcon(Res.drawable.mosque_02)
    ReminderType.DAILY_WORD -> UiIcon(Res.drawable.ic_quran_02)
    ReminderType.BEFORE_PRAYER -> UiIcon(Res.drawable.ic_accurate_prayer)
    null -> UiIcon(Res.drawable.ic_alarm_clock)
}

fun getRecurrenceText(recurrence: RecurrencePattern): String = when (recurrence) {
    RecurrencePattern.DAILY -> "كل يوم"
    RecurrencePattern.WEEKLY_THURSDAY -> "كل خميس"
    RecurrencePattern.WEEKLY -> "كل أسبوع"
    RecurrencePattern.ONCE -> "مرة واحدة"
}
