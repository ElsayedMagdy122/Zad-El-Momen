package dev.sayed.mehrabalmomen.domain.repository.prayer


import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.model.RescheduleResult

interface PrayerAlarmRepository {
    fun reschedule(prayers: List<PrayerAlarm>) : RescheduleResult
}