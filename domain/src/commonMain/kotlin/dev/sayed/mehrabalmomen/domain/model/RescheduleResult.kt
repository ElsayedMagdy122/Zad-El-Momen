package dev.sayed.mehrabalmomen.domain.model

sealed class RescheduleResult {
    object Success : RescheduleResult()
    object PermissionRequired : RescheduleResult()
}