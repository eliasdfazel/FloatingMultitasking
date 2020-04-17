/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/16/20 5:28 PM
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
import net.geekstools.floatshort.PRO.databinding.InAppBillingSubscriptionPurchaseViewBinding

class SubscriptionPurchase : Fragment() {

    private lateinit var inAppBillingSubscriptionPurchaseViewBinding: InAppBillingSubscriptionPurchaseViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState)
        inAppBillingSubscriptionPurchaseViewBinding = InAppBillingSubscriptionPurchaseViewBinding.inflate(layoutInflater)

        return inAppBillingSubscriptionPurchaseViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    override fun onDetach() {
        super.onDetach()

    }
}