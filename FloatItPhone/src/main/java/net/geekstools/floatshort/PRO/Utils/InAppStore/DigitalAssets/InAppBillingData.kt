/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/17/20 11:12 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets

import com.android.billingclient.api.BillingClient

class InAppBillingData {

    companion object SKU {
        val SKUS = HashMap<String, ArrayList<String>>()

        /**
         * One Time Purchase: Sku for Donation - Sku: donation
         **/
        const val InAppItemDonation = "donation"
        /**
         * One Time Purchase: Sku for Floating Widgets - Sku: floating.widgets
         **/
        const val InAppItemFloatingWidgets = "floating.widgets"
        /**
         * Subscription Purchase: Sku for Security Services - Sku: security.services
         **/
        const val InAppItemSecurityServices = "security.services"
        /**
         * Subscription Purchase: Sku for Search Engine - Sku: search.engines
         **/
        const val InAppItemSearchEngines = "search.engines"
    }

    init {
        InAppBillingData.SKUS[BillingClient.SkuType.INAPP] = arrayListOf(SKU.InAppItemDonation, SKU.InAppItemFloatingWidgets)
        InAppBillingData.SKUS[BillingClient.SkuType.SUBS] = arrayListOf(SKU.InAppItemSecurityServices, SKU.InAppItemSearchEngines)
    }

    /**
     * BillingClient.SkuType.INAPP
     * OR
     * BillingClient.SkuType.SUBS
     **/
    fun getAllSkusByType(@BillingClient.SkuType skuType: String) : ArrayList<String>? {

        return InAppBillingData.SKUS[skuType]
    }
}