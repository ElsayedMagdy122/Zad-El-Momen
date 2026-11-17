package dev.sayed.mehrabalmomen.presentation.screen.reminders

import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.presentation.base.UiIcon
import dev.sayed.mehrabalmomen.presentation.base.UiText
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.azkar_evening
import zadelmomen.presentation.generated.resources.azkar_morning
import zadelmomen.presentation.generated.resources.friday_sunnah
import zadelmomen.presentation.generated.resources.daily_quran_word
import zadelmomen.presentation.generated.resources.ic_alarm_clock
import zadelmomen.presentation.generated.resources.ic_accurate_prayer
import zadelmomen.presentation.generated.resources.ic_morning
import zadelmomen.presentation.generated.resources.ic_quran_02
import zadelmomen.presentation.generated.resources.ic_sleep
import zadelmomen.presentation.generated.resources.mosque_02

fun getReminderTitle(config: ReminderConfig): UiText {
    return when (config.type) {
        ReminderType.MORNING_AZKAR -> UiText.StringRes(Res.string.azkar_morning)
        ReminderType.EVENING_AZKAR -> UiText.StringRes(Res.string.azkar_evening)
        ReminderType.FRIDAY_SUNNAN -> UiText.StringRes(Res.string.friday_sunnah)
        ReminderType.DAILY_WORD -> UiText.StringRes(Res.string.daily_quran_word)
        ReminderType.BEFORE_PRAYER -> UiText.DynamicString("التنبيه قبل الصلاة")
        null -> UiText.DynamicString(config.title)
    }
}

fun getReminderTitle(type: ReminderType): UiText = when (type) {
    ReminderType.MORNING_AZKAR -> UiText.StringRes(Res.string.azkar_morning)
    ReminderType.EVENING_AZKAR -> UiText.StringRes(Res.string.azkar_evening)
    ReminderType.FRIDAY_SUNNAN -> UiText.StringRes(Res.string.friday_sunnah)
    ReminderType.DAILY_WORD -> UiText.StringRes(Res.string.daily_quran_word)
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
