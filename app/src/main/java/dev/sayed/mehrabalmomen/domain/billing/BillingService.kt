package dev.sayed.mehrabalmomen.domain.billing

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BillingService {
    val products: StateFlow<List<DonationProduct>>
    val events: SharedFlow<BillingEvent>
    val isBillingConnected: StateFlow<Boolean>
    fun refresh()
    fun donate(activity: Any, productId: String, quantity: Int = 1)
}
