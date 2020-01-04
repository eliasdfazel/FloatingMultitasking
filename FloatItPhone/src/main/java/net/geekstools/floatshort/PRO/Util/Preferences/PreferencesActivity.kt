/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/4/20 2:01 AM
 * Last modified 1/4/20 1:48 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.Preferences

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.res.Resources.Theme
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.preferences_activity_view.*
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Util.IAP.InAppBilling
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations
import kotlin.math.hypot


class PreferencesActivity : AppCompatActivity() {

    lateinit var functionsClass: FunctionsClass

    var FromWidgetsConfigurations: Boolean = false

    lateinit var rootLayout: View

    override fun getTheme(): Theme? {
        val theme = super.getTheme()
        if (PublicVariable.themeLightDark) {
            theme.applyStyle(R.style.GeeksEmpire_Preference_Light, true)
        } else if (!PublicVariable.themeLightDark) {
            theme.applyStyle(R.style.GeeksEmpire_Preference_Dark, true)
        }
        return theme
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences_activity_view)

        functionsClass = FunctionsClass(applicationContext, this@PreferencesActivity)

        if (functionsClass.appThemeTransparent()) {
            functionsClass.setThemeColorPreferences(fullPreferencesActivity, preferencesToolbar, true, getString(R.string.settingTitle), "${BuildConfig.VERSION_NAME}")
        } else {
            functionsClass.setThemeColorPreferences(fullPreferencesActivity, preferencesToolbar, false, getString(R.string.settingTitle), "${BuildConfig.VERSION_NAME}")
        }

        rootLayout = this.window.decorView
        rootLayout.visibility = View.INVISIBLE
        val viewTreeObserver = rootLayout.viewTreeObserver

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerPreferencesFragment, PreferencesFragment(), "PreferencesFragment")
                .commit()

        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble())
                    val circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, (functionsClass.displayX() / 2), (functionsClass.displayY() / 2), functionsClass.DpToInteger(55).toFloat(), finalRadius.toFloat())
                    circularReveal.duration = 1300
                    circularReveal.interpolator = AccelerateInterpolator()

                    rootLayout.visibility = View.VISIBLE
                    circularReveal.start()
                    rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    circularReveal.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            rootLayout.visibility = View.VISIBLE;
                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationStart(animation: Animator?) {

                        }
                    })
                }
            })
        } else {
            rootLayout.visibility = View.VISIBLE
        }

        FromWidgetsConfigurations = if (intent.hasExtra("FromWidgetsConfigurations")) intent.getBooleanExtra("FromWidgetsConfigurations", false) else false
    }

    override fun onStart() {
        super.onStart()

        giftIcon.setOnClickListener {
            startActivity(Intent(applicationContext, InAppBilling::class.java)
                    .putExtra("UserEmailAddress", functionsClass.readPreference(".UserInformation", "userEmail", null)))
        }

        facebookIcon.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook_app)))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun onBackPressed() {
        try {
            if (FromWidgetsConfigurations) {
                val intent = Intent(applicationContext, WidgetConfigurations::class.java)
                startActivity(intent)
            } else {
                if (PublicVariable.forceReload) {
                    PublicVariable.forceReload = false
                    functionsClass.overrideBackPressToMain(this@PreferencesActivity)
                }
            }
            val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble())
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, functionsClass.displayX() / 2, functionsClass.displayY() / 2, finalRadius.toFloat(), 0f)
            circularReveal.duration = 213
            circularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    rootLayout.visibility = View.INVISIBLE
                }
            })
            circularReveal.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Handler().postDelayed({
            this@PreferencesActivity.finish()
        }, 700)
    }
}