/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 10/5/21, 8:13 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.NetworkCheckpoint
import net.geekstools.floatshort.PRO.Utils.Functions.PreferencesIO
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions.setupInAppBillingUI
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase.OneTimePurchase
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.SubscriptionPurchase
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchaseFlowController
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchasesCheckpoint
import net.geekstools.floatshort.PRO.databinding.InAppBillingViewBinding

class InitializeInAppBilling : AppCompatActivity(), PurchaseFlowController {

    val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    val preferencesIO: PreferencesIO by lazy {
        PreferencesIO(applicationContext)
    }

    private val inAppBillingData: InAppBillingData by lazy {
        InAppBillingData()
    }

    private val networkCheckpoint: NetworkCheckpoint by lazy {
        NetworkCheckpoint(applicationContext)
    }

    private var oneTimePurchase: OneTimePurchase? = null
    private var subscriptionPurchase: SubscriptionPurchase? = null

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

        if (networkCheckpoint.networkConnection()
                && intent.hasExtra(Entry.PurchaseType) && intent.hasExtra(Entry.ItemToPurchase)) {

            when(intent.getStringExtra(Entry.PurchaseType)) {
                Entry.OneTimePurchase -> {
                    oneTimePurchase = OneTimePurchase().apply {
                        purchaseFlowController = this@InitializeInAppBilling
                        inAppBillingData = this@InitializeInAppBilling.inAppBillingData
                    }
                    oneTimePurchase!!.arguments = Bundle().apply {
                        putString(Entry.ItemToPurchase, intent.getStringExtra(Entry.ItemToPurchase))
                    }

                    supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, 0)
                            .replace(R.id.fragmentPlaceHolder, oneTimePurchase!!, "One Time Purchase")
                            .commit()
                }
                Entry.SubscriptionPurchase -> {
                    subscriptionPurchase = SubscriptionPurchase().apply {
                        purchaseFlowController = this@InitializeInAppBilling
                        inAppBillingData = this@InitializeInAppBilling.inAppBillingData
                    }
                    subscriptionPurchase!!.arguments = Bundle().apply {
                        putString(Entry.ItemToPurchase, intent.getStringExtra(Entry.ItemToPurchase))
                    }

                    supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, 0)
                            .replace(R.id.fragmentPlaceHolder, subscriptionPurchase!!, "One Time Purchase")
                            .commit()
                }
            }
        } else {

            this@InitializeInAppBilling.finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        this@InitializeInAppBilling.finish()
    }

    override fun purchaseFlowInitial(billingResult: BillingResult?) {
        Log.d(this@InitializeInAppBilling.javaClass.simpleName, "${billingResult?.debugMessage}")

        when (billingResult?.responseCode) {
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                preferencesIO
                        .savePreference(".PurchasedItem",
                                intent.getStringExtra(Entry.ItemToPurchase),
                                true)

                preferencesIO
                    .savePreference(".SubscribedItem",
                        intent.getStringExtra(Entry.ItemToPurchase),
                        true)

                this@InitializeInAppBilling.finish()
            }
        }
    }

    override fun purchaseFlowDisrupted(errorMessage: String?) {
        Log.d(this@InitializeInAppBilling.javaClass.simpleName, "Purchase Flow Disrupted: ${errorMessage}")

        oneTimePurchase?.let {
            supportFragmentManager
                    .beginTransaction()
                    .remove(it)
        }

        subscriptionPurchase?.let {
            supportFragmentManager
                    .beginTransaction()
                    .remove(it)
        }

        val snackbar = Snackbar.make(inAppBillingViewBinding.root,
                getString(R.string.purchaseFlowDisrupted),
                Snackbar.LENGTH_INDEFINITE)
        snackbar.setBackgroundTint(PublicVariable.primaryColor)
        snackbar.setTextColor(PublicVariable.colorLightDarkOpposite)
        snackbar.setActionTextColor(getColor(R.color.default_color_game_light))
        snackbar.setAction(Html.fromHtml(getString(R.string.retry), Html.FROM_HTML_MODE_COMPACT)) {

        }
        snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {

            override fun onDismissed(transientBottomBar: Snackbar?, transitionEvent: Int) {
                super.onDismissed(transientBottomBar, transitionEvent)

                if (!this@InitializeInAppBilling.isFinishing) {
                    startActivity(Intent(applicationContext, InitializeInAppBilling::class.java).apply {
                        putExtra(InitializeInAppBilling.Entry.PurchaseType, intent.getStringExtra(Entry.PurchaseType))
                        putExtra(InitializeInAppBilling.Entry.ItemToPurchase, intent.getStringExtra(Entry.ItemToPurchase))
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.down_up, android.R.anim.fade_out).toBundle())
                }
            }
        })

        val snackbarView = snackbar.view
        val frameLayoutLayoutParams = snackbarView.layoutParams as FrameLayout.LayoutParams
        frameLayoutLayoutParams.gravity = Gravity.BOTTOM
        snackbarView.layoutParams = frameLayoutLayoutParams

        snackbar.show()
    }

    override fun purchaseFlowSucceeded(productDetails: ProductDetails) {
        Log.d(this@InitializeInAppBilling.javaClass.simpleName, "Purchase Flow Succeeded: ${productDetails}")

    }

    override fun purchaseFlowPaid(billingClient: BillingClient, purchase: Purchase) {
        Log.d(this@InitializeInAppBilling.javaClass.simpleName, "Purchase Flow Paid: ${purchase}")

        PurchasesCheckpoint.purchaseAcknowledgeProcess(billingClient, purchase, BillingClient.ProductType.INAPP)

        preferencesIO
                .savePreference(".PurchasedItem",
                        purchase.products.first(),
                        true)

        preferencesIO
            .savePreference(".SubscribedItem",
                purchase.products.first(),
                true)

        this@InitializeInAppBilling.finish()
    }

    override fun purchaseFlowPaid(productDetails: ProductDetails) {
        Log.d(this@InitializeInAppBilling.javaClass.simpleName, "Purchase Flow Paid: ${productDetails}")

        preferencesIO
                .savePreference(".PurchasedItem",
                        productDetails.productId,
                        true)

        preferencesIO
            .savePreference(".SubscribedItem",
                productDetails.productId,
                true)

        this@InitializeInAppBilling.finish()
    }
}