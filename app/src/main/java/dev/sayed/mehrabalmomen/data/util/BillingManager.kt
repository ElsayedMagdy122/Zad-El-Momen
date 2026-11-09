package dev.sayed.mehrabalmomen.data.util

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import dev.sayed.mehrabalmomen.BuildConfig
import dev.sayed.mehrabalmomen.domain.billing.BillingEvent
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.billing.DonationProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class BillingManager(
    context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : BillingService {

    private val productIds = listOf(
        BuildConfig.SUPPORT_1,
        BuildConfig.SUPPORT_5,
        BuildConfig.SUPPORT_10,
        BuildConfig.SUPPORT_25,
        BuildConfig.SUPPORT_100
    )

    private val productDetailsMap = mutableMapOf<String, ProductDetails>()

    // Prevents processing the same purchase token twice at the same time
    private val processingTokens = mutableSetOf<String>()
    private val processingLock = Mutex()

    private val _products = MutableStateFlow<List<DonationProduct>>(emptyList())
    override val products = _products.asStateFlow()

    private val _events = MutableSharedFlow<BillingEvent>()
    override val events = _events.asSharedFlow()

    private val _isBillingConnected = MutableStateFlow(false)
    override val isBillingConnected = _isBillingConnected.asStateFlow()

    private val listener = PurchasesUpdatedListener { result, purchases ->
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    scope.launch { handlePurchase(purchase, isFromListener = true) }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {}
            else -> {
                scope.launch { _events.emit(BillingEvent.GeneralError) }
            }
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(listener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    init {
        start()
    }

    fun start() {
        if (billingClient.isReady) {
            _isBillingConnected.value = true
            scope.launch {
                loadProducts()
                processOwnedPurchases()
            }
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isBillingConnected.value = true
                    scope.launch {
                        loadProducts()
                        processOwnedPurchases()
                    }
                } else {
                    _isBillingConnected.value = false
                }
            }

            override fun onBillingServiceDisconnected() {
                _isBillingConnected.value = false
                start()
            }
        })
    }

    override fun refresh() {
        if (billingClient.isReady) {
            _isBillingConnected.value = true
            scope.launch {
                loadProducts()
                processOwnedPurchases()
            }
        } else {
            start()
        }
    }

    private suspend fun loadProducts() {
        val productList = productIds.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        val result = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params)
        }
        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            val detailsList = result.productDetailsList.orEmpty()
            productDetailsMap.clear()
            detailsList.forEach { details ->
                productDetailsMap[details.productId] = details
            }

            _products.value = detailsList.map { details ->
                val offer = details.oneTimePurchaseOfferDetails
                DonationProduct(
                    id = details.productId,
                    title = details.title,
                    description = details.description,
                    price = offer?.formattedPrice ?: "",
                    priceAmountMicros = offer?.priceAmountMicros ?: 0L,
                    rawProduct = details
                )
            }.sortedBy { it.priceAmountMicros }
        }
    }

    override fun donate(
        activity: Any,
        productId: String,
        quantity: Int
    ) {
        val activityInstance = activity as? Activity ?: return
        val productDetails = productDetailsMap[productId] ?: return
        val offerToken = productDetails.oneTimePurchaseOfferDetails?.offerToken ?: return

        val productDetailsParamsList: List<BillingFlowParams.ProductDetailsParams> = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activityInstance, billingFlowParams)
    }

    private suspend fun processOwnedPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        val result = withContext(Dispatchers.IO) {
            billingClient.queryPurchasesAsync(params)
        }
        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            result.purchasesList.forEach { purchase ->
                handlePurchase(purchase, isFromListener = false)
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase, isFromListener: Boolean) {
        val token = purchase.purchaseToken

        // Skip if this purchase is already being processed
        val shouldProcess = processingLock.withLock { processingTokens.add(token) }
        if (!shouldProcess) return

        try {
            when (purchase.purchaseState) {
                Purchase.PurchaseState.PURCHASED -> {
                    val consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(token)
                        .build()

                    val consumeResult = withContext(Dispatchers.IO) {
                        billingClient.consumePurchase(consumeParams)
                    }
                    if (consumeResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        if (isFromListener) {
                            _events.emit(BillingEvent.Success)
                        }
                    } else {
                        if (isFromListener) {
                            _events.emit(BillingEvent.ProcessingError)
                        }
                    }
                }
                Purchase.PurchaseState.PENDING -> {
                    if (isFromListener) {
                        _events.emit(BillingEvent.Pending)
                    }
                }
                else -> {}
            }
        } finally {
            processingLock.withLock { processingTokens.remove(token) }
        }
    }
}
