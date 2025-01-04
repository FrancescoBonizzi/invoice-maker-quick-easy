package com.francescobonizzi.invoiceEstimateReceiptMaker.logic

import android.app.Activity
import com.android.billingclient.api.*
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IInvoicesRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.SharedPreferencesHelper

/** Gestione degli acquisti in-app */
class BillingHandler(
    private val _activity: Activity,
    private val _onError: (error: String) -> Unit,
    private val _onNotPurchasedUnlimitedInvoicesLoaded: (sku: SkuDetails) -> Unit,
    private val _onAlreadyPurchasedUnlimitedInvoicesLoaded: (billingHandler: BillingHandler) -> Unit,
    private val _onPurchasedItem: () -> Unit,
    private val _onPurchasedItemCancel: () -> Unit
)
{
    private lateinit var _billingClient: BillingClient
    private var _unlimitedInvoiceSkuName: String = "unlimited_invoices"

    private val purchasesUpdateListener =
        PurchasesUpdatedListener { billingResult, purchases ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null)
            {
                for (purchase in purchases)
                {
                    // Se l'acquisto è confermato
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED)
                    {
                        // E non è ancora stato confermato allo store
                        if (!purchase.isAcknowledged)
                        {
                            // Salvo l'acquisto in locale, così lo recupero più facilmente
                            setUnlimitedInvoicesPurchased()

                            // E lo confermo allo store
                            val acknowledgePurchaseParams = AcknowledgePurchaseParams
                                .newBuilder()
                                .setPurchaseToken(purchase.purchaseToken)

                            _billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {
                                _onPurchasedItem()
                            }
                        }
                    }
                }
            }
            else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED)
            {
                _onPurchasedItemCancel()
            }
            else
            {
                _onError("Purchase resulted in error. Response: ${billingResult.debugMessage}")
            }
        }

    init
    {
        _billingClient = BillingClient.newBuilder(_activity)
            .setListener(purchasesUpdateListener)
            .enablePendingPurchases()
            .build()

        _billingClient.startConnection(object : BillingClientStateListener
        {
            override fun onBillingSetupFinished(billingResult: BillingResult)
            {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK)
                {
                    refreshUnlimitedInvoicesSku()
                }
                else
                {
                    _onError("onBillingSetupFinished failed. Response: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected()
            {
                _onError("Cannot connect to Google Play for InApp purchase!")
            }
        })
    }

    fun destroy()
    {
        _billingClient.endConnection()
    }

    private fun refreshUnlimitedInvoicesSku() {
        _billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { result, purchasesList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchasesList.isNotEmpty()) {
                    purchasesList.forEach { purchase ->
                        if (purchase.skus.contains(_unlimitedInvoiceSkuName)) {
                            // Salva l'acquisto in locale per recuperarlo più facilmente
                            setUnlimitedInvoicesPurchased()
                            _onAlreadyPurchasedUnlimitedInvoicesLoaded(this)
                            return@queryPurchasesAsync
                        }
                    }
                    _onError("Didn't find the $_unlimitedInvoiceSkuName purchase. But there are ${purchasesList.size} purchases")
                } else {
                    querySkuDetails()
                }
            } else {
                _onError("Error retrieving purchases: ${result.debugMessage}")
            }
        }
    }

    private fun querySkuDetails()
    {
        val skuList = ArrayList<String>()
        skuList.add(_unlimitedInvoiceSkuName)

        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()

        _billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->

            try
            {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK)
                {
                    if (skuDetailsList == null || skuDetailsList.count() == 0)
                    {
                        throw Exception("List of InApp purchases came empty from the BillingClient!")
                    }

                    _onNotPurchasedUnlimitedInvoicesLoaded(skuDetailsList[0])
                }
                else
                {
                    throw Exception("querySkuDetailsAsync failed. Response: ${billingResult.debugMessage}")
                }
            }
            catch (exception: Exception)
            {
                _onError(exception.toString())
            }
        }
    }

    fun buyUnlimitedInvoices(itemToPurchase: SkuDetails)
    {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(itemToPurchase)
            .build()
        _billingClient.launchBillingFlow(_activity, flowParams).responseCode
    }

    companion object
    {
        private const val freeInvoices: Int = 5
        private const val unlimitedInvoicesBoughtKey = "UnlimitedInvoiceBought"

        fun canCreateNewInvoices(invoicesRepository: IInvoicesRepository): Boolean
        {
            val hasUsedAllFreeInvoices = invoicesRepository.getIdentity() >= freeInvoices
            return isUnlimitedInvoicesPurchased() || !hasUsedAllFreeInvoices
        }

        fun setUnlimitedInvoicesPurchased()
        {
            setUnlimitedInvoicesPurchase(isPurchased = true)
        }

        fun setUnlimitedInvoicesNOTPurchased()
        {
            setUnlimitedInvoicesPurchase(isPurchased = false)
        }

        private fun setUnlimitedInvoicesPurchase(isPurchased: Boolean)
        {
            SharedPreferencesHelper.putBool(unlimitedInvoicesBoughtKey, isPurchased)
        }

        fun isUnlimitedInvoicesPurchased(): Boolean
        {
            // All'avvio dell'app, in background setto questa variabile
            return SharedPreferencesHelper.getOrPutBool(unlimitedInvoicesBoughtKey, false)
        }
    }
}