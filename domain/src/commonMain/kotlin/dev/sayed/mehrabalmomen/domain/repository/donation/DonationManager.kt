package dev.sayed.mehrabalmomen.domain.repository.donation

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

data class ProductPrice(val productId: String, val formattedPrice: String)

interface DonationManager {
    val productDetails: StateFlow<List<ProductPrice>>
    val purchaseSuccess: SharedFlow<Unit>
    fun queryProducts(productIds: List<String>)
    fun launchDonationFlow(productId: String)
}
