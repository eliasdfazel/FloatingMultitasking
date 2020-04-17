/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/17/20 12:06 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Extensions

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.WindowManager
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling

fun InitializeInAppBilling.setupInAppBillingUI() {

    inAppBillingViewBinding.root.setBackgroundColor(PublicVariable.colorLightDark)

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f)
    window.navigationBarColor = functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f)
    if (PublicVariable.themeLightDark) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        if (Build.VERSION.SDK_INT > 25) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    inAppBillingViewBinding.shareFloatIt.setOnClickListener {

        val sharingText: String = getString(R.string.shareTitle) + "\n" +
                "" + getString(R.string.shareSummary) + "\n" +
                "" + "${getString(R.string.play_store_link)}${packageName}"

        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, sharingText)
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(this@apply)
        }
    }

    inAppBillingViewBinding.rateFloatIt.setOnClickListener {

        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.play_store_link) + packageName)))
    }
}