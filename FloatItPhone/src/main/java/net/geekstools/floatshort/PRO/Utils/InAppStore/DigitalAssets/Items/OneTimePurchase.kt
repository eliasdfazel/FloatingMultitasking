/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/17/20 12:59 AM
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
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.SkuDetailsParams
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InAppBillingData
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchaseFlowController
import net.geekstools.floatshort.PRO.databinding.InAppBillingOneTimePurchaseViewBinding

class OneTimePurchase (private val purchaseFlowController: PurchaseFlowController,
                       private val inAppBillingData: InAppBillingData) : Fragment() {

    lateinit var inAppBillingOneTimePurchaseViewBinding: InAppBillingOneTimePurchaseViewBinding

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

        val billingClient = BillingClient.newBuilder(requireActivity()).setListener { billingResult, mutableList ->

        }.enablePendingPurchases().build()

        val skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(listOfItem)
                .setType(BillingClient.SkuType.INAPP)
                .build()

        billingClient.startConnection(object : BillingClientStateListener {

            override fun onBillingServiceDisconnected() {

                purchaseFlowController.purchaseFlowDisrupted(null)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {

                billingClient.querySkuDetailsAsync(skuDetailsParams) { queryBillingResult, skuDetailsListInApp ->
                    FunctionsClassDebug.PrintDebug("Billing Result: ${queryBillingResult.debugMessage} | Sku Details List In App Purchase: $skuDetailsListInApp")

                    when (queryBillingResult.responseCode) {
                        BillingClient.BillingResponseCode.ERROR -> {

                            purchaseFlowController.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.USER_CANCELED -> {

                            purchaseFlowController.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {

                            purchaseFlowController.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> {

                            purchaseFlowController.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {

                            purchaseFlowController.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {

                            purchaseFlowController.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {

                            purchaseFlowController.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                            if (skuDetailsListInApp.isNotEmpty()) {

                                purchaseFlowController.purchaseFlowPaid(skuDetails = skuDetailsListInApp[0])
                            }
                        }
                        BillingClient.BillingResponseCode.OK -> {

                            purchaseFlowController.purchaseFlowSucceeded(skuDetails = skuDetailsListInApp[0])

                            if (skuDetailsListInApp.isNotEmpty()) {

                                val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
                                firebaseRemoteConfig.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build())
                                firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
                                firebaseRemoteConfig.fetchAndActivate().addOnSuccessListener {



                                }.addOnFailureListener {

                                }
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onDetach() {
        super.onDetach()

    }
}