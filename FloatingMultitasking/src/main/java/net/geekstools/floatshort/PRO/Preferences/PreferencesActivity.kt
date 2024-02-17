/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/11/20 10:45 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Preferences

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.Resources.Theme
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.ApplicationThemeController
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData
import net.geekstools.floatshort.PRO.Widgets.WidgetConfigurations
import net.geekstools.floatshort.PRO.databinding.PreferencesActivityViewBinding
import kotlin.math.hypot

class PreferencesActivity : AppCompatActivity() {

    lateinit var functionsClassLegacy: FunctionsClassLegacy

    private val applicationThemeController: ApplicationThemeController by lazy {
        ApplicationThemeController(applicationContext)
    }

    private var fromWidgetsConfigurations: Boolean = false

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

    lateinit var preferencesActivityViewBinding: PreferencesActivityViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesActivityViewBinding = PreferencesActivityViewBinding.inflate(layoutInflater)
        setContentView(preferencesActivityViewBinding.root)

        functionsClassLegacy = FunctionsClassLegacy(applicationContext)

        functionsClassLegacy.loadSavedColor()
        functionsClassLegacy.checkLightDarkTheme()

        applicationThemeController.setThemeColorPreferences(this, preferencesActivityViewBinding.fullPreferencesActivity, preferencesActivityViewBinding.preferencesToolbar, getString(R.string.settingTitle), BuildConfig.VERSION_NAME)

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
                    val finalRadius = hypot(functionsClassLegacy.displayX().toDouble(), functionsClassLegacy.displayY().toDouble())
                    val circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, (functionsClassLegacy.displayX() / 2), (functionsClassLegacy.displayY() / 2), functionsClassLegacy.DpToInteger(55).toFloat(), finalRadius.toFloat())
                    circularReveal.duration = 1300
                    circularReveal.interpolator = AccelerateInterpolator()

                    rootLayout.visibility = View.VISIBLE
                    circularReveal.start()
                    rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    circularReveal.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            rootLayout.visibility = View.VISIBLE;
                        }

                        override fun onAnimationCancel(animation: Animator) {

                        }

                        override fun onAnimationStart(animation: Animator) {

                        }
                    })
                }
            })
        } else {
            rootLayout.visibility = View.VISIBLE
        }

        fromWidgetsConfigurations = if (intent.hasExtra("FromWidgetsConfigurations")) intent.getBooleanExtra("FromWidgetsConfigurations", false) else false
    }

    override fun onStart() {
        super.onStart()

        preferencesActivityViewBinding.gift.setOnClickListener {

            startActivity(Intent(applicationContext, InitializeInAppBilling::class.java).apply {
                putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.OneTimePurchase)
                putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemDonation)
            }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.down_up, android.R.anim.fade_out).toBundle())
        }

        preferencesActivityViewBinding.facebookIcon.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook_app)))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

    }

    override fun onBackPressed() {
        try {
            if (fromWidgetsConfigurations) {
                val intent = Intent(applicationContext, WidgetConfigurations::class.java)
                startActivity(intent)
            } else {
                if (PublicVariable.forceReload) {
                    PublicVariable.forceReload = false

                    functionsClassLegacy.overrideBackPressToMain(this@PreferencesActivity, this@PreferencesActivity)
                }
            }
            val finalRadius = hypot(functionsClassLegacy.displayX().toDouble(), functionsClassLegacy.displayY().toDouble())
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, functionsClassLegacy.displayX() / 2, functionsClassLegacy.displayY() / 2, finalRadius.toFloat(), 0f)
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

        Handler(Looper.getMainLooper()).postDelayed({
            this@PreferencesActivity.finish()
        }, 700)
    }
}