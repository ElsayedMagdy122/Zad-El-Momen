package dev.sayed.mehrabalmomen.data.util

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingManager(private val context: Context) {

    private val _productDetails = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetails = _productDetails.asStateFlow()

    private val _purchaseSuccess = MutableSharedFlow<Purchase>(extraBufferCapacity = 1)
    val purchaseSuccess = _purchaseSuccess.asSharedFlow()

    private var pendingProductIds: List<String>? = null
    private var billingClient: BillingClient? = null

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

    fun queryProducts(productIds: List<String>) {
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
                    _productDetails.value = result.productDetailsList ?: emptyList()
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
                    _purchaseSuccess.tryEmit(purchase)
                }
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productId: String) {
        val details = _productDetails.value.find { it.productId == productId }
        if (details == null) {
            return
        }

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