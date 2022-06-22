/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 10/5/21, 8:04 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import net.geekstools.floatshort.PRO.Utils.Functions.Debug.Companion.PrintDebug
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.NetworkCheckpoint
import net.geekstools.floatshort.PRO.Utils.Functions.PreferencesIO
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData

class PurchasesCheckpoint(var appCompatActivity: AppCompatActivity) : PurchasesUpdatedListener {

    val functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(appCompatActivity)

    val preferencesIO: PreferencesIO = PreferencesIO(appCompatActivity)

    val networkCheckpoint: NetworkCheckpoint by lazy {
        NetworkCheckpoint(appCompatActivity)
    }

    fun trigger() : BillingClient {

        val billingClient = BillingClient.newBuilder(appCompatActivity)
                .setListener(this@PurchasesCheckpoint)
                .enablePendingPurchases().build()

        //In-App Billing
        if (networkCheckpoint.networkConnection()) {

            //Restore Purchased Item
            billingClient.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(billingResult: BillingResult) {

                    billingResult.let {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            preferencesIO.savePreference(".PurchasedItem", InAppBillingData.SKU.InAppItemFloatingWidgets, false)

                            appCompatActivity.lifecycleScope.async {

                                val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                                    .setProductType(BillingClient.ProductType.INAPP)
                                    .build()

                                billingClient.queryPurchasesAsync(queryPurchasesParams).purchasesList.let { purchases ->

                                    for (purchase in purchases) {
                                        PrintDebug("*** Purchased Item: $purchase ***")

                                        preferencesIO.savePreference(".PurchasedItem", purchase.products.first(), true)

                                        //Consume Donation
                                        if (purchase.products.first() == InAppBillingData.SKU.InAppItemDonation
                                            && functionsClassLegacy.alreadyDonated()) {

                                            val consumeResponseListener = ConsumeResponseListener { billingResult, purchaseToken ->
                                                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                                    PrintDebug("*** Consumed Item: $purchaseToken ***")

                                                    preferencesIO.savePreference(".PurchasedItem", purchase.products.first(), false)
                                                }
                                            }
                                            val consumeParams = ConsumeParams.newBuilder()
                                            consumeParams.setPurchaseToken(purchase.purchaseToken)
                                            billingClient.consumeAsync(consumeParams.build(), consumeResponseListener)
                                        }

                                        PurchasesCheckpoint.purchaseAcknowledgeProcess(billingClient, purchase, BillingClient.ProductType.INAPP)
                                    }

                                }

                            }

                        }
                    }
                }

                override fun onBillingServiceDisconnected() {

                }
            })

            //Restore Subscribed Item
            billingClient.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(billingResult: BillingResult) {

                    billingResult.let {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            preferencesIO.savePreference(".SubscribedItem", InAppBillingData.SKU.InAppItemSecurityServices, false)
                            preferencesIO.savePreference(".SubscribedItem", InAppBillingData.SKU.InAppItemSearchEngines, false)

                            appCompatActivity.lifecycleScope.async {

                                val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()

                                billingClient.queryPurchasesAsync(queryPurchasesParams).purchasesList.let { purchases ->

                                    for (purchase in purchases) {
                                        PrintDebug("*** Subscribed Item: $purchase ***")

                                        preferencesIO.savePreference(".SubscribedItem", purchase.products.first(), true)

                                        PurchasesCheckpoint.purchaseAcknowledgeProcess(billingClient, purchase, BillingClient.ProductType.SUBS)
                                    }
                                }

                            }

                        }
                    }
                }

                override fun onBillingServiceDisconnected() {

                }
            })
        }

        return billingClient
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchasesList: MutableList<Purchase>?) {

        billingResult.let {
            if (!purchasesList.isNullOrEmpty()) {

                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                    }
                    BillingClient.BillingResponseCode.OK -> {

                    }
                    else -> {

                    }
                }
            }
        }
    }

    companion object {

        fun purchaseAcknowledgeProcess(billingClient: BillingClient, purchase: Purchase, purchaseType: String) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                PrintDebug("*** ${purchase.skus.first()} Purchase Acknowledged: ${purchase.isAcknowledged} ***")

                if (!purchase.isAcknowledged) {

                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)

                    val aPurchaseResult: BillingResult = billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())

                    PrintDebug("*** Purchased Acknowledged Result: ${purchase.skus.first()} -> ${aPurchaseResult.debugMessage} ***")
                }
            }
        }
    }
}