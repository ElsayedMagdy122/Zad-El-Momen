package dev.sayed.mehrabalmomen.presentation.screen.reminders

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType

fun getReminderTitle(type: ReminderType): Int = when (type) {

    ReminderType.MORNING_AZKAR ->
        R.string.azkar_morning

    ReminderType.EVENING_AZKAR ->
        R.string.azkar_evening

    ReminderType.FRIDAY_SUNNAN ->
        R.string.friday_sunnah

    ReminderType.DAILY_WORD ->
        R.string.daily_quran_word
}
fun getReminderIcon(config: ReminderConfig): Int = when (config.type) {
    ReminderType.MORNING_AZKAR ->
        R.drawable.ic_morning

    ReminderType.EVENING_AZKAR ->
        R.drawable.ic_sleep

    ReminderType.FRIDAY_SUNNAN ->
        R.drawable.mosque_02

    ReminderType.DAILY_WORD ->
        R.drawable.ic_quran_02
}