/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/16/20 7:09 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.billingclient.api.SkuDetails
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions.setupInAppBillingUI
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchaseFlowController
import net.geekstools.floatshort.PRO.databinding.InAppBillingViewBinding

class InitializeInAppBilling : AppCompatActivity(), PurchaseFlowController {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    val oneTimePurchase: InAppBillingData by lazy {
        InAppBillingData()
    }


    object Entry {
        const val PurchaseType = "PurchaseType"
        const val ItemToPurchase = "ItemToPurchase"

        const val OneTimePurchase = "OneTimePurchase"
        const val SubscriptionPurchase = "SubscriptionPurchase"
    }

    lateinit var inAppBillingViewBinding: InAppBillingViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inAppBillingViewBinding = InAppBillingViewBinding.inflate(layoutInflater)
        setContentView(inAppBillingViewBinding.root)

        setupInAppBillingUI()

        if (functionsClass.networkConnection()
                && intent.hasExtra(Entry.PurchaseType) && intent.hasExtra(Entry.ItemToPurchase)) {

            when(intent.getStringExtra(Entry.PurchaseType)) {
                Entry.OneTimePurchase -> {
                    val oneTimePurchase: Fragment = OneTimePurchase(this@InitializeInAppBilling, oneTimePurchase)
                    oneTimePurchase.arguments = Bundle().apply {
                        putString(Entry.ItemToPurchase, intent.getStringExtra(Entry.ItemToPurchase))
                    }

                    supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, 0)
                            .replace(R.id.fragmentPlaceHolder, oneTimePurchase, "One Time Purchase")
                            .commit()
                }
                Entry.SubscriptionPurchase -> {
                    val subscriptionPurchase: Fragment = SubscriptionPurchase(this@InitializeInAppBilling, oneTimePurchase)
                    subscriptionPurchase.arguments = Bundle().apply {
                        putString(Entry.ItemToPurchase, intent.getStringExtra(Entry.ItemToPurchase))
                    }

                    supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, 0)
                            .replace(R.id.fragmentPlaceHolder, subscriptionPurchase, "One Time Purchase")
                            .commit()
                }
            }
        } else {

            this@InitializeInAppBilling.finish()
        }
    }

    override fun purchaseFlowDisrupted() {

    }

    override fun purchaseFlowSucceeded(skuDetails: SkuDetails) {

    }

    override fun purchaseFlowPaid(skuDetails: SkuDetails) {

    }
}