/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/16/20 4:32 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class InitialInAppBilling : AppCompatActivity() {

    object Entry {
        const val purchaseType = "PurchaseType"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra(Entry.purchaseType)) {

        }
    }
}