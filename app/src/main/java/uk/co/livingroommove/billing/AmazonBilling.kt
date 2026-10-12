package uk.co.livingroommove.billing

import android.content.Context
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uk.co.livingroommove.core.Offer

data class BillingState(val configured: Boolean = false, val offers: List<Offer> = emptyList(),
    val message: String = "billing_unavailable")
interface BillingGateway {
    val state: StateFlow<BillingState>
    fun refresh()
    fun purchase(sku: String)
    fun restore()
}

/** SDK transport is wired; commerce remains disabled until server RVS verification is configured.
 * The shared RVS secret belongs only on a backend. A callback alone never grants entitlement. */
class AmazonBilling(private val context: Context) : BillingGateway, PurchasingListener {
    private val mutable = MutableStateFlow(BillingState())
    override val state: StateFlow<BillingState> = mutable
    private val skus = setOf("livingroommove.monthly", "livingroommove.annual")
    private val authenticationKeyPresent = context.assets.list("")?.contains("AppstoreAuthenticationKey.pem") == true
    private val verifierConfigured = false // Deliberate release blocker: see docs/BILLING.md.
    private val ready get() = authenticationKeyPresent && verifierConfigured
    override fun refresh() {
        if (!ready) { mutable.value = BillingState(); return }
        PurchasingService.registerListener(context, this)
        PurchasingService.getUserData()
        PurchasingService.getProductData(skus)
        PurchasingService.getPurchaseUpdates(false)
    }
    override fun purchase(sku: String) {
        if (!ready || mutable.value.offers.none { it.sku == sku }) return
        PurchasingService.purchase(sku)
    }
    override fun restore() {
        if (!ready) { mutable.value = BillingState(message = "restore_unavailable"); return }
        PurchasingService.getPurchaseUpdates(true)
    }
    override fun onUserDataResponse(response: UserDataResponse) {
        if (response.requestStatus != UserDataResponse.RequestStatus.SUCCESSFUL)
            mutable.value = BillingState(message = "billing_unavailable")
        // Production verifier must bind receipts to response.userData.userId and marketplace.
    }
    override fun onProductDataResponse(response: ProductDataResponse) {
        mutable.value = if (response.requestStatus == ProductDataResponse.RequestStatus.SUCCESSFUL)
            BillingState(ready, response.productData.values.map { Offer(it.sku, it.title, it.price) }, "billing_ready")
        else BillingState(message = "billing_unavailable")
    }
    override fun onPurchaseResponse(response: PurchaseResponse) {
        mutable.value = mutable.value.copy(message = "receipt_verification_required")
        // Do not fulfil or unlock until server RVS accepts userId/receiptId, SKU, status and expiry.
    }
    override fun onPurchaseUpdatesResponse(response: PurchaseUpdatesResponse) {
        if (response.requestStatus == PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL && response.hasMore())
            PurchasingService.getPurchaseUpdates(false)
        mutable.value = mutable.value.copy(message = "receipt_verification_required")
        // Production: verify and persist each page, deduplicate receipts; reconcile refunds/cancellation.
    }
}
