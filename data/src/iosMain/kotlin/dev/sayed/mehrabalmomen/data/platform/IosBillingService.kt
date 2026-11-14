package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.billing.BillingEvent
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.billing.DonationProduct
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class IosBillingService : BillingService {
    override val products: StateFlow<List<DonationProduct>> = MutableStateFlow(emptyList())
    override val events: SharedFlow<BillingEvent> = MutableSharedFlow()
    override val isBillingConnected: StateFlow<Boolean> = MutableStateFlow(true)

    override fun refresh() {}
    override fun donate(activity: Any, productId: String, quantity: Int) {}
}
