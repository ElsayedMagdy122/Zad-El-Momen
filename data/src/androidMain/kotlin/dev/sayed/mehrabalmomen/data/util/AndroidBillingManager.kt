package dev.sayed.mehrabalmomen.data.util

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import dev.sayed.mehrabalmomen.domain.repository.donation.DonationManager
import dev.sayed.mehrabalmomen.domain.repository.donation.ProductPrice
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidBillingManager(private val context: Context) : DonationManager {

    private val _productDetails = MutableStateFlow<List<ProductPrice>>(emptyList())
    override val productDetails = _productDetails.asStateFlow()

    private val _purchaseSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val purchaseSuccess = _purchaseSuccess.asSharedFlow()

    private var pendingProductIds: List<String>? = null
    private var billingClient: BillingClient? = null
    
    private val cachedProductDetails = mutableMapOf<String, ProductDetails>()
    
    private var currentActivity: Activity? = null

    fun setActivity(activity: Activity) {
        currentActivity = activity
    }

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    purchases.forEach { processPurchase(it) }
                }
            }
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .build()
        startConnection()
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    pendingProductIds?.let { queryProducts(it) }
                }
            }

            override fun onBillingServiceDisconnected() {
                startConnection()
            }
        })
    }

    override fun queryProducts(productIds: List<String>) {
        pendingProductIds = productIds

        if (billingClient?.isReady == true) {
            val productList = productIds.map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            }
            val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

            billingClient?.queryProductDetailsAsync(params) { billingResult, result ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val details = result.productDetailsList ?: emptyList()
                    details.forEach { cachedProductDetails[it.productId] = it }
                    _productDetails.value = details.map { 
                        ProductPrice(it.productId, it.oneTimePurchaseOfferDetails?.formattedPrice ?: "") 
                    }
                }
            }
        }
    }

    private fun processPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient?.acknowledgePurchase(params) { result ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _purchaseSuccess.tryEmit(Unit)
                }
            }
        }
    }

    override fun launchDonationFlow(productId: String) {
        val activity = currentActivity ?: return
        val details = cachedProductDetails[productId] ?: return

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(details)
                        .build()
                )
            ).build()
        billingClient?.launchBillingFlow(activity, flowParams)
    }
}
