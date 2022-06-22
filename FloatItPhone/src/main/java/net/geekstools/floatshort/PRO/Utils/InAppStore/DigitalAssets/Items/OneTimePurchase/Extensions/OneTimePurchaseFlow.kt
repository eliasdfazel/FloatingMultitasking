/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/15/20 5:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase.Extensions

import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase.OneTimePurchase

fun OneTimePurchase.oneTimePurchaseFlow(productDetails: ProductDetails) {

    inAppBillingOneTimePurchaseViewBinding.centerPurchaseButton.root.setOnClickListener {

        purchaseFlowCommand(productDetails)
    }

    inAppBillingOneTimePurchaseViewBinding.bottomPurchaseButton.root.setOnClickListener {

        purchaseFlowCommand(productDetails)
    }
}

private fun OneTimePurchase.purchaseFlowCommand(productDetails: ProductDetails) {

    val billingFlowParams = BillingFlowParams.newBuilder()
        .setProductDetailsParamsList(listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        ))
        .build()

    val billingResult = billingClient.launchBillingFlow(requireActivity() as InitializeInAppBilling, billingFlowParams)
    purchaseFlowController?.purchaseFlowInitial(billingResult)
}