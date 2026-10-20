package dev.sayed.mehrabalmomen.domain.billing

sealed interface BillingEvent {
    data object Success : BillingEvent
    data object ProcessingError : BillingEvent
    data object Pending : BillingEvent
    data object GeneralError : BillingEvent
}
