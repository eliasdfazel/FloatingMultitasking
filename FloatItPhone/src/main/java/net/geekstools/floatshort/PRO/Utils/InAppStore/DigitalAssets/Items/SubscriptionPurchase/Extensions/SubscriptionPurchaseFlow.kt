/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/15/20 5:42 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.Extensions

import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.SubscriptionPurchase

fun SubscriptionPurchase.subscriptionPurchaseFlow(skuDetails: SkuDetails) {

    inAppBillingSubscriptionPurchaseViewBinding.centerPurchaseButton.root.setOnClickListener {

        purchaseFlowCommand(skuDetails)
    }

    inAppBillingSubscriptionPurchaseViewBinding.bottomPurchaseButton.root.setOnClickListener {

        purchaseFlowCommand(skuDetails)
    }
}

private fun SubscriptionPurchase.purchaseFlowCommand(skuDetails: SkuDetails) {
    val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

    val billingResult = billingClient.launchBillingFlow(requireActivity() as InitializeInAppBilling, billingFlowParams)
    purchaseFlowController?.purchaseFlowInitial(billingResult)
}