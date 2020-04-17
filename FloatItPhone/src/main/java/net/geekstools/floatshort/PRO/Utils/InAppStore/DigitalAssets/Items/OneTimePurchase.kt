/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/16/20 7:10 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetailsParams
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InAppBillingData
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchaseFlowController
import net.geekstools.floatshort.PRO.databinding.InAppBillingOneTimePurchaseViewBinding

class OneTimePurchase (private val purchaseFlowController: PurchaseFlowController,
                       private val inAppBillingData: InAppBillingData) : Fragment() {

    private lateinit var inAppBillingOneTimePurchaseViewBinding: InAppBillingOneTimePurchaseViewBinding

    private val listOfItem: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listOfItem.add(arguments?.getString(InitializeInAppBilling.Entry.ItemToPurchase) ?: InAppBillingData.InAppItemDonation)
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState)
        inAppBillingOneTimePurchaseViewBinding = InAppBillingOneTimePurchaseViewBinding.inflate(layoutInflater)

        return inAppBillingOneTimePurchaseViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val billingClient = BillingClient.newBuilder(requireActivity()).setListener { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchaseList != null) {

            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {

            } else {

            }
        }.enablePendingPurchases().build()

        val skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(listOfItem)
                .setType(BillingClient.SkuType.INAPP)
                .build()
        billingClient.querySkuDetailsAsync(skuDetailsParams) { billingResult, skuDetailsListInApp ->
            FunctionsClassDebug.PrintDebug("Billing Result: $billingResult | Sku Details List In App Purchase: $skuDetailsListInApp")

            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.ERROR -> {

                    purchaseFlowController.purchaseFlowDisrupted()
                }
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {

                    purchaseFlowController.purchaseFlowDisrupted()
                }
                BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> {

                    purchaseFlowController.purchaseFlowDisrupted()
                }
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {

                    purchaseFlowController.purchaseFlowDisrupted()
                }
                BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {

                    purchaseFlowController.purchaseFlowDisrupted()
                }
                BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {

                    purchaseFlowController.purchaseFlowDisrupted()
                }
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                    if (skuDetailsListInApp.isNotEmpty()) {
                        purchaseFlowController.purchaseFlowPaid(skuDetails = skuDetailsListInApp[0])
                    }
                }
                BillingClient.BillingResponseCode.OK -> {
                    purchaseFlowController.purchaseFlowSucceeded(skuDetails = skuDetailsListInApp[0])

                    if (skuDetailsListInApp.isNotEmpty()) {

                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()

    }
}