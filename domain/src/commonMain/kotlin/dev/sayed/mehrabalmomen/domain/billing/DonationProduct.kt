package dev.sayed.mehrabalmomen.domain.billing

data class DonationProduct(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val priceAmountMicros: Long,
    val rawProduct: Any? = null
)

object SupportTiers {
    const val TIER_1 = "dev_sayed_support_1"
    const val TIER_5 = "dev_sayed_support_5"
    const val TIER_10 = "dev_sayed_support_10"
    const val TIER_25 = "dev_sayed_support_25"
    const val TIER_100 = "dev_sayed_support_100"

    val ALL = listOf(TIER_1, TIER_5, TIER_10, TIER_25, TIER_100)
}
