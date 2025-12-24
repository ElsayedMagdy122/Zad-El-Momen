package dev.sayed.mehrabalmomen.domain.repository


import dev.sayed.mehrabalmomen.domain.model.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.model.RescheduleResult

interface AzanSchedulerRepository {
    fun reschedule(prayers: List<PrayerAlarm>) : RescheduleResult
}