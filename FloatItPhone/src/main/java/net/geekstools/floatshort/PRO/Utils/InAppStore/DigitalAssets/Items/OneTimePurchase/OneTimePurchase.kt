/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 12/15/20 9:55 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.android.billingclient.api.*
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.button.MaterialButton
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.Debug
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions.convertToItemTitle
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions.convertToRemoteConfigDescriptionKey
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions.convertToRemoteConfigPriceInformation
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions.convertToStorageScreenshotsDirectory
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase.Extensions.oneTimePurchaseFlow
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase.Extensions.setScreenshots
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.OneTimePurchase.Extensions.setupOneTimePurchaseUI
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchaseFlowController
import net.geekstools.floatshort.PRO.databinding.InAppBillingOneTimePurchaseViewBinding
import java.util.*

class OneTimePurchase : Fragment(), View.OnClickListener, PurchasesUpdatedListener {

    lateinit var billingClient: BillingClient

    private val billingClientBuilder: BillingClient.Builder by lazy {
        BillingClient.newBuilder(requireActivity())//.build()
    }

    var purchaseFlowController: PurchaseFlowController? = null
    lateinit var inAppBillingData: InAppBillingData

    private val requestManager: RequestManager by lazy {
        Glide.with(requireContext())
    }

    private var itemToPurchase: String = InAppBillingData.SKU.InAppItemDonation

    val mapIndexDrawable = TreeMap<Int, Drawable>()
    val mapIndexURI = TreeMap<Int, Uri>()

    var screenshotsNumber: Int = 6
    var glideLoadCounter: Int = 0

    lateinit var inAppBillingOneTimePurchaseViewBinding: InAppBillingOneTimePurchaseViewBinding

    /**
     * Callback After Purchase Dialogue Flow Get Closed
     **/
    override fun onPurchasesUpdated(billingResult: BillingResult, purchasesList: MutableList<Purchase>?) {
        Log.d(this@OneTimePurchase.javaClass.simpleName, "Purchases Updated: ${billingResult?.debugMessage}")

        billingResult?.let {
            if (!purchasesList.isNullOrEmpty()) {

                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                        purchaseFlowController?.purchaseFlowPaid(billingClient, purchasesList[0])
                    }
                    BillingClient.BillingResponseCode.OK -> {

                        purchaseFlowController?.purchaseFlowPaid(billingClient, purchasesList[0])
                    }
                    else -> {

                        purchaseFlowController?.purchaseFlowDisrupted(billingResult.debugMessage)
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemToPurchase = (arguments?.getString(InitializeInAppBilling.Entry.ItemToPurchase) ?: InAppBillingData.SKU.InAppItemDonation)
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState)
        inAppBillingOneTimePurchaseViewBinding = InAppBillingOneTimePurchaseViewBinding.inflate(layoutInflater)

        return inAppBillingOneTimePurchaseViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOneTimePurchaseUI()

        billingClient = billingClientBuilder.setListener(this@OneTimePurchase).enablePendingPurchases().build()
        billingClient.startConnection(object : BillingClientStateListener {

            override fun onBillingServiceDisconnected() {

                purchaseFlowController?.purchaseFlowDisrupted(null)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {

                val skuDetailsParams = SkuDetailsParams.newBuilder()
                        .setSkusList(listOf(itemToPurchase))
                        .setType(BillingClient.SkuType.INAPP)
                        .build()

                billingClient.querySkuDetailsAsync(skuDetailsParams) { queryBillingResult, skuDetailsListInApp ->
                    Debug.PrintDebug("Billing Result: ${queryBillingResult.debugMessage} | Sku Details List In App Purchase: $skuDetailsListInApp")

                    when (queryBillingResult.responseCode) {
                        BillingClient.BillingResponseCode.ERROR -> {

                            purchaseFlowController?.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.USER_CANCELED -> {

                            purchaseFlowController?.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {

                            purchaseFlowController?.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> {

                            purchaseFlowController?.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {

                            purchaseFlowController?.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {

                            purchaseFlowController?.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {

                            purchaseFlowController?.purchaseFlowDisrupted(queryBillingResult.debugMessage)
                        }
                        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                            if (!skuDetailsListInApp.isNullOrEmpty()) {

                                purchaseFlowController?.purchaseFlowPaid(productDetails = skuDetailsListInApp[0])
                            }
                        }
                        BillingClient.BillingResponseCode.OK -> {

                            if (!skuDetailsListInApp.isNullOrEmpty()) {

                                purchaseFlowController?.purchaseFlowSucceeded(productDetails = skuDetailsListInApp[0])

                                oneTimePurchaseFlow(skuDetailsListInApp[0])

                                if (itemToPurchase == InAppBillingData.SKU.InAppItemDonation) {

                                    inAppBillingOneTimePurchaseViewBinding.itemTitleView.visibility = View.GONE
                                    inAppBillingOneTimePurchaseViewBinding.itemDescriptionView.text =
                                            Html.fromHtml("<br/>" +
                                                    "<big>${skuDetailsListInApp[0].title}</big>" +
                                                    "<br/>" +
                                                    "<br/>" +
                                                    "${skuDetailsListInApp[0].description}" +
                                                    "<br/>")

                                    (inAppBillingOneTimePurchaseViewBinding
                                            .centerPurchaseButton.root as MaterialButton).text = getString(R.string.donate)
                                    (inAppBillingOneTimePurchaseViewBinding
                                            .bottomPurchaseButton.root as MaterialButton).visibility = View.INVISIBLE

                                    inAppBillingOneTimePurchaseViewBinding.itemScreenshotsView.visibility = View.GONE

                                } else {

                                    inAppBillingOneTimePurchaseViewBinding.itemTitleView.text = (itemToPurchase.convertToItemTitle())

                                    val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
                                    firebaseRemoteConfig.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build())
                                    firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
                                    firebaseRemoteConfig.fetchAndActivate().addOnSuccessListener {

                                        inAppBillingOneTimePurchaseViewBinding
                                                .itemDescriptionView.text = Html.fromHtml(firebaseRemoteConfig.getString(itemToPurchase.convertToRemoteConfigDescriptionKey()))

                                        (inAppBillingOneTimePurchaseViewBinding
                                                .centerPurchaseButton.root as MaterialButton).text = firebaseRemoteConfig.getString(itemToPurchase.convertToRemoteConfigPriceInformation())
                                        (inAppBillingOneTimePurchaseViewBinding
                                                .bottomPurchaseButton.root as MaterialButton).text = firebaseRemoteConfig.getString(itemToPurchase.convertToRemoteConfigPriceInformation())

                                        val firebaseStorage = FirebaseStorage.getInstance()
                                        val firebaseStorageReference = firebaseStorage.reference
                                        firebaseStorageReference
                                                .child("Assets/Images/Screenshots/${itemToPurchase.convertToStorageScreenshotsDirectory()}/IAP.Demo/")
                                                .listAll()
                                                .addOnSuccessListener { itemsStorageReference ->

                                                    screenshotsNumber = itemsStorageReference.items.size

                                                    itemsStorageReference.items.forEachIndexed { index, storageReference ->

                                                        storageReference.downloadUrl.addOnSuccessListener { screenshotLink ->

                                                            requestManager
                                                                    .load(screenshotLink)
                                                                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                                    .addListener(object : RequestListener<Drawable> {
                                                                        override fun onLoadFailed(glideException: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {

                                                                            return false
                                                                        }

                                                                        override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                                                            glideLoadCounter++

                                                                            val beforeToken: String = screenshotLink.toString().split("?alt=media&token=")[0]
                                                                            val drawableIndex = beforeToken[beforeToken.length - 5].toString().toInt()

                                                                            mapIndexDrawable[drawableIndex] = resource
                                                                            mapIndexURI[drawableIndex] = screenshotLink

                                                                            if (glideLoadCounter == screenshotsNumber) {

                                                                                setScreenshots()
                                                                            }

                                                                            return false
                                                                        }

                                                                    }).submit()
                                                        }

                                                    }

                                                }

                                    }.addOnFailureListener {

                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        if (requestManager.isPaused) {
            requestManager.resumeRequests()
        }
    }

    override fun onPause() {
        super.onPause()

        requestManager.pauseAllRequests()
    }

    override fun onDetach() {
        super.onDetach()

        billingClient.endConnection()

        itemToPurchase = InAppBillingData.SKU.InAppItemDonation
    }

    override fun onClick(view: View?) {

        when(view) {
            is ImageView -> {

                if (view.tag.toString().contains("_Youtube")) {

                    val youtubeVideoId = view.tag.toString()
                            .split("/Assets%2FImages%2FScreenshots%2FFloatingWidgets%2FIAP.Demo%2F")[1]
                            .split("?alt=media&token")[0]
                            .split("_Youtube")[0]
                    val youtubeLink = "https://www.youtube.com/watch?v=".plus(youtubeVideoId)

                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(youtubeLink)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        requireContext().startActivity(this@apply)
                    }

                } else {

                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(view.getTag().toString())
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        requireContext().startActivity(this@apply)
                    }

                }

            }
        }
    }
}