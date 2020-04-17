/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/17/20 12:55 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils

import com.android.billingclient.api.SkuDetails

interface PurchaseFlowController {
    fun purchaseFlowDisrupted(errorMessage: String?)
    fun purchaseFlowSucceeded(skuDetails: SkuDetails)
    fun purchaseFlowPaid(skuDetails: SkuDetails)
}