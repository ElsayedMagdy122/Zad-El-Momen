package dev.sayed.mehrabalmomen.domain.billing

data class DonationProduct(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val priceAmountMicros: Long,
    val rawProduct: Any? = null
)
