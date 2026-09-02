package dev.sayed.mehrabalmomen.domain.mapper

import dev.sayed.mehrabalmomen.domain.model.ReminderNotification
import dev.sayed.mehrabalmomen.domain.model.ReminderType

/**
 * Mapper extension to transform a [ReminderType] into a displayable [ReminderNotification].
 * This keeps the presentation text logic within the domain/logic layer but separated from the model itself.
 */
fun ReminderType.toNotification(): ReminderNotification {
    return ReminderNotification(
        id = this.alarmId,
        title = when (this) {
            ReminderType.MORNING_AZKAR -> "أذكار الصباح"
            ReminderType.EVENING_AZKAR -> "أذكار المساء"
            ReminderType.FRIDAY_SUNNAN -> "سنن الجمعة"
            ReminderType.DAILY_WORD -> "الورد اليومي"
        },
        body = when (this) {
            ReminderType.MORNING_AZKAR -> "لا تنس أذكار الصباح"
            ReminderType.EVENING_AZKAR -> "لا تنس أذكار المساء"
            ReminderType.FRIDAY_SUNNAN -> "أكثر من الصلاة على النبي واقرأ سورة الكهف"
            ReminderType.DAILY_WORD -> "حان وقت وردك القرآني اليومي"
        }
    )
}
