/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/18/20 1:07 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils

import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug.Companion.PrintDebug
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InAppBillingData

class PurchasesCheckpoint(var appCompatActivity: AppCompatActivity) {

    val functionsClass: FunctionsClass = FunctionsClass(appCompatActivity)

    fun trigger() : BillingClient {

        val billingClient = BillingClient.newBuilder(appCompatActivity).setListener { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchaseList != null) {

            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {

            } else {

            }
        }.enablePendingPurchases().build()

        //In-App Billing
        if (functionsClass.networkConnection()) {

            //Restore Purchased Item
            billingClient.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(billingResult: BillingResult) {

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        functionsClass.savePreference(".PurchasedItem", InAppBillingData.InAppItemFloatingWidgets, false)

                        val purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList

                        for (purchase in purchases) {
                            PrintDebug("*** Purchased Item: $purchase ***")

                            functionsClass.savePreference(".PurchasedItem", purchase.sku, true)

                            //Consume Donation
                            if (purchase.sku == InAppBillingData.InAppItemDonation
                                    && functionsClass.alreadyDonated()) {

                                val consumeResponseListener = ConsumeResponseListener { billingResult, purchaseToken ->
                                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                        PrintDebug("*** Consumed Item: $purchaseToken ***")

                                        functionsClass.savePreference(".PurchasedItem", purchase.sku, false)
                                    }
                                }
                                val consumeParams = ConsumeParams.newBuilder()
                                consumeParams.setPurchaseToken(purchase.purchaseToken)
                                billingClient.consumeAsync(consumeParams.build(), consumeResponseListener)
                            }

                            PurchasesCheckpoint.purchaseAcknowledgeProcess(billingClient, purchase, BillingClient.SkuType.INAPP)
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {

                }
            })

            //Restore Subscribed Item
            billingClient.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(billingResult: BillingResult) {

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        functionsClass.savePreference(".SubscribedItem", InAppBillingData.InAppItemSecurityServices, false)

                        val purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS).purchasesList
                        for (purchase in purchases) {
                            PrintDebug("*** Subscribed Item: $purchase ***")

                            functionsClass.savePreference(".SubscribedItem", purchase.sku, true)

                            PurchasesCheckpoint.purchaseAcknowledgeProcess(billingClient, purchase, BillingClient.SkuType.SUBS)
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {

                }
            })
        }

        return billingClient
    }

    companion object {

        fun purchaseAcknowledgeProcess(billingClient: BillingClient, purchase: Purchase, purchaseType: String) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                PrintDebug("*** ${purchase.sku} Purchase Acknowledged: ${purchase.isAcknowledged} ***")

                if (!purchase.isAcknowledged) {

                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)

                    val aPurchaseResult: BillingResult = billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())

                    PrintDebug("*** Purchased Acknowledged Result: ${purchase.sku} -> ${aPurchaseResult.debugMessage} ***")
                }
            }
        }
    }
}