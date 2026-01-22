package dev.sayed.mehrabalmomen.domain.repository


import dev.sayed.mehrabalmomen.domain.model.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.model.RescheduleResult

interface PrayerAlarmRepository {
    fun reschedule(prayers: List<PrayerAlarm>) : RescheduleResult
}