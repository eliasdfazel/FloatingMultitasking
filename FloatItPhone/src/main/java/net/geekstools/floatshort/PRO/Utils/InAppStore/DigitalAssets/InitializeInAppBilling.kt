/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/16/20 6:17 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions.setupInAppBillingUI
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase
import net.geekstools.floatshort.PRO.databinding.InAppBillingViewBinding

class InitializeInAppBilling : AppCompatActivity() {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
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
                    val oneTimePurchase: Fragment = OneTimePurchase()
                    oneTimePurchase.arguments = Bundle().apply {
                        putString(intent.getStringExtra(Entry.ItemToPurchase), InAppBillingData.InAppItemDonation)
                    }

                    supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, 0)
                            .replace(R.id.fragmentPlaceHolder, oneTimePurchase, "One Time Purchase")
                            .commit()
                }
                Entry.SubscriptionPurchase -> {
                    val subscriptionPurchase: Fragment = SubscriptionPurchase()
                    subscriptionPurchase.arguments = Bundle().apply {
                        putString(intent.getStringExtra(Entry.ItemToPurchase), InAppBillingData.InAppItemDonation)
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
}