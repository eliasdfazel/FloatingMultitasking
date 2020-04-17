/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/16/20 7:06 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils

import com.android.billingclient.api.SkuDetails

interface PurchaseFlowController {
    fun purchaseFlowDisrupted()
    fun purchaseFlowSucceeded(skuDetails: SkuDetails)
    fun purchaseFlowPaid(skuDetails: SkuDetails)
}