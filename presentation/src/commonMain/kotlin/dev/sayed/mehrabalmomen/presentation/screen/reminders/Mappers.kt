package dev.sayed.mehrabalmomen.presentation.screen.reminders

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.presentation.base.UiIcon
import dev.sayed.mehrabalmomen.presentation.base.UiText

fun getReminderTitle(type: ReminderType): UiText = when (type) {
    ReminderType.MORNING_AZKAR -> UiText.StringResource(R.string.azkar_morning)
    ReminderType.EVENING_AZKAR -> UiText.StringResource(R.string.azkar_evening)
    ReminderType.FRIDAY_SUNNAN -> UiText.StringResource(R.string.friday_sunnah)
    ReminderType.DAILY_WORD -> UiText.StringResource(R.string.daily_quran_word)
}

fun getReminderIcon(config: ReminderConfig): UiIcon = when (config.type) {
    ReminderType.MORNING_AZKAR -> UiIcon(R.drawable.ic_morning)
    ReminderType.EVENING_AZKAR -> UiIcon(R.drawable.ic_sleep)
    ReminderType.FRIDAY_SUNNAN -> UiIcon(R.drawable.mosque_02)
    ReminderType.DAILY_WORD -> UiIcon(R.drawable.ic_quran_02)
}
