/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/16/20 4:18 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets

import com.android.billingclient.api.BillingClient

class InAppBillingData {

    companion object SKU {
        val SKUS = HashMap<String, ArrayList<String>>()

        const val iapDonation = "donation"
        const val iapFloatingWidgets = "floating.widgets"
        const val iapSecurityServices = "security.services"
        const val iapSearchEngines = "search.engines"
    }

    init {
        InAppBillingData.SKUS[BillingClient.SkuType.INAPP] = arrayListOf(SKU.iapDonation, SKU.iapFloatingWidgets)
        InAppBillingData.SKUS[BillingClient.SkuType.SUBS] = arrayListOf(SKU.iapSecurityServices, SKU.iapSearchEngines)
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