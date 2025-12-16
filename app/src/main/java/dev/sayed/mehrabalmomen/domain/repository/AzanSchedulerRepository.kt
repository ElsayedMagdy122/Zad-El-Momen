package dev.sayed.mehrabalmomen.domain.repository


import dev.sayed.mehrabalmomen.domain.model.PrayerAlarm

interface AzanSchedulerRepository {
    fun reschedule(prayers: List<PrayerAlarm>) : RescheduleResult
}

sealed class RescheduleResult {
    object Success : RescheduleResult()
    object PermissionRequired : RescheduleResult()
}