/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 12/8/20 11:42 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import net.geekstools.floatshort.PRO.R

class ApplicationThemeController (private val context: Context) {

    private val functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(context)

    fun setThemeColorFloating(instanceOfActivity: AppCompatActivity, rootView: View) {


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

    fun setThemeColorPreferences(instanceOfActivity: FragmentActivity, rootView: View, preferencesToolbar: ConstraintLayout, title: String, subTitle: String) {

        rootView.setBackgroundColor(PublicVariable.colorLightDark)

        preferencesToolbar.setBackgroundColor(PublicVariable.primaryColor)
        (preferencesToolbar.findViewById(R.id.titlePreferences) as TextView).text = Html.fromHtml("<font color='" + context.getColor(R.color.light) + "'>" + title + "</font>", Html.FROM_HTML_MODE_COMPACT)

        instanceOfActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        instanceOfActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        if (PublicVariable.themeLightDark) {
            instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT > 25) {
                instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }

        instanceOfActivity.window.statusBarColor = PublicVariable.primaryColor
        instanceOfActivity.window.navigationBarColor = PublicVariable.colorLightDark

    }

}