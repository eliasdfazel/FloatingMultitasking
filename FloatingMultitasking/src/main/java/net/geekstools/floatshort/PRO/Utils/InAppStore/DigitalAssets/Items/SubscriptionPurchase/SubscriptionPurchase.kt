/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/14/20 4:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase

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
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
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
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions.convertToItemTitle
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions.convertToRemoteConfigDescriptionKey
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions.convertToStorageScreenshotsDirectory
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.Extensions.setScreenshots
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.Extensions.setupOneTimePurchaseUI
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.SubscriptionPurchase.Extensions.subscriptionPurchaseFlow
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchaseFlowController
import net.geekstools.floatshort.PRO.databinding.InAppBillingSubscriptionPurchaseViewBinding
import java.util.TreeMap

class SubscriptionPurchase : Fragment(), View.OnClickListener, PurchasesUpdatedListener {

    lateinit var billingClient: BillingClient

    private val billingClientBuilder: BillingClient.Builder by lazy {
        BillingClient.newBuilder(requireActivity())//.build()
    }

    var purchaseFlowController: PurchaseFlowController? = null
    lateinit var inAppBillingData: InAppBillingData

    private val requestManager: RequestManager by lazy {
        Glide.with(requireContext())
    }

    private val listOfItems: ArrayList<QueryProductDetailsParams.Product> = ArrayList<QueryProductDetailsParams.Product>()

    val mapIndexDrawable = TreeMap<Int, Drawable>()
    val mapIndexURI = TreeMap<Int, Uri>()

    var screenshotsNumber: Int = 6
    var glideLoadCounter: Int = 0

    lateinit var inAppBillingSubscriptionPurchaseViewBinding: InAppBillingSubscriptionPurchaseViewBinding

    /**
     * Callback After Purchase Dialogue Flow Get Closed
     **/
    override fun onPurchasesUpdated(billingResult: BillingResult, purchasesList: MutableList<Purchase>?) {
        Log.d(this@SubscriptionPurchase.javaClass.simpleName, "Purchases Updated: ${billingResult?.debugMessage}")

        billingResult.let {
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

        listOfItems.add(QueryProductDetailsParams.Product.newBuilder()
            .setProductId(arguments?.getString(InitializeInAppBilling.Entry.ItemToPurchase) ?: InAppBillingData.SKU.InAppItemDonation)
            .setProductType(BillingClient.ProductType.SUBS)
            .build())
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState)
        inAppBillingSubscriptionPurchaseViewBinding = InAppBillingSubscriptionPurchaseViewBinding.inflate(layoutInflater)

        return inAppBillingSubscriptionPurchaseViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOneTimePurchaseUI()

        billingClient = billingClientBuilder.setListener(this@SubscriptionPurchase).enablePendingPurchases(PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .enablePrepaidPlans()
            .build()).build()
        billingClient.startConnection(object : BillingClientStateListener {

            override fun onBillingServiceDisconnected() {

                purchaseFlowController?.purchaseFlowDisrupted(null)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {

                val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                        .setProductList(listOfItems)
                        .build()

                billingClient.queryProductDetailsAsync(queryProductDetailsParams) { queryBillingResult, productsDetailsListInApp ->

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

                            if (productsDetailsListInApp.productDetailsList.isNotEmpty()) {

                                purchaseFlowController?.purchaseFlowPaid(productDetails = productsDetailsListInApp.productDetailsList[0])
                            }
                        }
                        BillingClient.BillingResponseCode.OK -> {

                            if (productsDetailsListInApp.productDetailsList.isNotEmpty()) {

                                purchaseFlowController?.purchaseFlowSucceeded(productDetails = productsDetailsListInApp.productDetailsList[0])

                                subscriptionPurchaseFlow(productsDetailsListInApp.productDetailsList[0])

                                if (listOfItems.isNotEmpty()) {


                                    val queriedProduct = QueryProductDetailsParams.Product.newBuilder()
                                        .setProductId(arguments?.getString(InitializeInAppBilling.Entry.ItemToPurchase) ?: InAppBillingData.SKU.InAppItemDonation)
                                        .setProductType(BillingClient.ProductType.SUBS)
                                        .build()

                                    if (listOfItems[0] == queriedProduct) {

                                        requireActivity().finish()

                                    } else {

                                        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
                                        firebaseRemoteConfig.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build())
                                        firebaseRemoteConfig.fetchAndActivate().addOnSuccessListener {

                                            activity?.runOnUiThread {

                                                inAppBillingSubscriptionPurchaseViewBinding.itemTitleView.text = (productsDetailsListInApp.productDetailsList.first().productId.convertToItemTitle())

                                                inAppBillingSubscriptionPurchaseViewBinding.itemDescriptionView.text = Html.fromHtml(firebaseRemoteConfig.getString(productsDetailsListInApp.productDetailsList.first().productId.convertToRemoteConfigDescriptionKey()), Html.FROM_HTML_MODE_COMPACT)

                                                (inAppBillingSubscriptionPurchaseViewBinding.centerPurchaseButton.root as MaterialButton).text = try {
                                                    productsDetailsListInApp.productDetailsList.first().subscriptionOfferDetails!!.first().pricingPhases.pricingPhaseList[1].formattedPrice
                                                } catch (e: Exception) {
                                                    productsDetailsListInApp.productDetailsList.first().subscriptionOfferDetails!!.first().pricingPhases.pricingPhaseList[0].formattedPrice
                                                }
                                                (inAppBillingSubscriptionPurchaseViewBinding.bottomPurchaseButton.root as MaterialButton).text = try {
                                                    productsDetailsListInApp.productDetailsList.first().subscriptionOfferDetails!!.first().pricingPhases.pricingPhaseList[1].formattedPrice
                                                } catch (e: Exception) {
                                                    productsDetailsListInApp.productDetailsList.first().subscriptionOfferDetails!!.first().pricingPhases.pricingPhaseList[0].formattedPrice
                                                }

                                            }

                                            val storagePath = "/FloatingMultitasking/Assets/Images/Screenshots/${productsDetailsListInApp.productDetailsList.first().productId.convertToStorageScreenshotsDirectory()}"
                                            Log.d(this@SubscriptionPurchase.javaClass.simpleName, "Storage Path: ${storagePath}")

                                            FirebaseStorage.getInstance()
                                                .reference
                                                .child(storagePath)
                                                .listAll().addOnSuccessListener { itemsStorageReference ->

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

        listOfItems.clear()
    }

    override fun onClick(view: View?) {

        when(view) {
            is ImageView -> {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(view.getTag().toString())
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    requireContext().startActivity(this@apply)
                }
            }
        }
    }
}