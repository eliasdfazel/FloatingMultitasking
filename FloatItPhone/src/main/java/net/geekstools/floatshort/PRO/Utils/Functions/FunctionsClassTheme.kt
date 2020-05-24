/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/23/20 8:34 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class FunctionsClassTheme (private val context: Context) {

    private val functionsClass: FunctionsClass = FunctionsClass(context)

    fun setThemeColorFloating(instanceOfActivity: AppCompatActivity, rootView: View, applyTransparency: Boolean) {

        if (applyTransparency) {

            rootView.setBackgroundColor(functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f))

            instanceOfActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            instanceOfActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (PublicVariable.themeLightDark) {
                instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT > 25) {
                    instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }

            instanceOfActivity.window.statusBarColor = functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f)
            instanceOfActivity.window.navigationBarColor = functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f)

        } else {

            rootView.setBackgroundColor(PublicVariable.colorLightDark)

            instanceOfActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            instanceOfActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (PublicVariable.themeLightDark) {
                instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT > 25) {
                    instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }

            instanceOfActivity.window.statusBarColor = PublicVariable.colorLightDark
            instanceOfActivity.window.navigationBarColor = PublicVariable.colorLightDark

        }
    }
}