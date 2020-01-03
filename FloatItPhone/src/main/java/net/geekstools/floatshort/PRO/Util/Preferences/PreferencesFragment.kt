/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/3/20 12:15 AM
 * Last modified 1/3/20 12:11 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.Preferences


import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Html
import androidx.preference.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Util.IAP.InAppBilling
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingManager
import net.geekstools.floatshort.PRO.Util.InteractionObserver.InteractionObserver
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.PinPassword.HandlePinPassword

class PreferencesFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {


    lateinit var functionsClass: FunctionsClass

    lateinit var sharedPreferences: SharedPreferences

    lateinit var themeColor: ListPreference
    lateinit var stick: ListPreference

    lateinit var stable: SwitchPreference
    lateinit var cache: SwitchPreference
    lateinit var themeTrans: SwitchPreference
    lateinit var smart: SwitchPreference
    lateinit var blur: SwitchPreference
    lateinit var observe: SwitchPreference
    lateinit var notification: SwitchPreference
    lateinit var floatingSplash: SwitchPreference
    lateinit var freeForm: SwitchPreference

    lateinit var pinPassword: Preference
    lateinit var shapes: Preference
    lateinit var autotrans: Preference
    lateinit var sizes: Preference
    lateinit var delayPressHold: Preference
    lateinit var flingSensitivity: Preference
    lateinit var boot: Preference
    lateinit var lite: Preference
    lateinit var support: Preference
    lateinit var whatsnew: Preference
    lateinit var adApp: Preference

    lateinit var runnablePressHold: Runnable
    val handlerPressHold = Handler()

    var touchingDelay = false
    var FromWidgetsConfigurations: Boolean = false
    var currentTheme: Boolean = false

    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    var betaChangeLog: String = "net.geekstools.floatshort.PRO"
    var betaVersionCode: String = "0"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_screen, rootKey)

        functionsClass = FunctionsClass(context, activity)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        stable = findPreference("stable")!!
        cache = findPreference("cache")!!
        themeTrans = findPreference("transparent")!!
        blur = findPreference("blur")!!
        smart = findPreference("smart")!!
        observe = findPreference("observe")!!
        notification = findPreference("notification")!!
        floatingSplash = findPreference("floatingSplash")!!
        freeForm = findPreference("freeForm")!!

        boot = findPreference("boot")!!
        pinPassword = findPreference("pinPassword")!!
        shapes = findPreference("shapes")!!
        autotrans = findPreference("hide")!!
        sizes = findPreference("sizes")!!
        flingSensitivity = findPreference("flingSensitivity")!!
        delayPressHold = findPreference("delayPressHold")!!
        lite = findPreference("lite")!!
        adApp = findPreference("app")!!
        whatsnew = findPreference("whatsnew")!!
        support = findPreference("support")!!

        themeColor = findPreference("themeColor")!!
        stick = findPreference("stick")!!

        val sticky = sharedPreferences.getString("stick", "1")
        if (sticky == "1") {
            stick.summary = getString(R.string.leftEdge)
        } else if (sticky == "2") {
            stick.summary = getString(R.string.rightEdge)
        }

        val bootUpPreference = sharedPreferences.getString("boot", "1")
        boot.summary = when (bootUpPreference) {
            "0" -> {
                getString(R.string.boot_none)
            }
            "1" -> {
                getString(R.string.shortcuts)
            }
            "2" -> {
                getString(R.string.floatingCategory)
            }
            "3" -> {
                getString(R.string.boot_warning)
            }
            else -> {
                getString(R.string.boot_none)
            }
        }

        functionsClass.checkLightDarkTheme()
        val appTheme = sharedPreferences.getString("themeColor", "2")
        if (appTheme == "1") {
            themeColor.summary = getString(R.string.light)
            PublicVariable.themeLightDark = true
        } else if (appTheme == "2") {
            themeColor.summary = getString(R.string.dark)
            PublicVariable.themeLightDark = false
        } else if (appTheme == "3") {
            functionsClass.checkLightDarkTheme()
            themeColor.summary = getString(R.string.dynamic)
        }

        delayPressHold.summary = functionsClass.readDefaultPreference("delayPressHold", 333).toString() + " " + getString(R.string.millis)

        cache.setOnPreferenceClickListener {
            if (PublicVariable.Stable) {
                context?.stopService(Intent(context, BindServices::class.java))
                Handler().postDelayed({
                    context?.startService(Intent(context, BindServices::class.java))
                }, 333)
            } else {
                if (sharedPreferences.getBoolean("cache", true)) {
                    context?.startService(Intent(context, BindServices::class.java))
                } else if (!sharedPreferences.getBoolean("cache", true)) {
                    if (PublicVariable.floatingCounter == 0) {
                        context?.stopService(Intent(context, BindServices::class.java))
                    }
                    functionsClass.saveDefaultPreference("LitePreferences", false)
                }
            }
            false
        }

        stable.setOnPreferenceClickListener {
            if (sharedPreferences.getBoolean("stable", true)) {
                PublicVariable.Stable = true
                context?.startService(Intent(context, BindServices::class.java))
            } else if (!sharedPreferences.getBoolean("stable", true)) {
                PublicVariable.Stable = false
                if (PublicVariable.floatingCounter == 0) {
                    context?.stopService(Intent(context, BindServices::class.java))
                }
                functionsClass.saveDefaultPreference("LitePreferences", false)
            }
            false
        }

        themeTrans.setOnPreferenceClickListener {
            if (functionsClass.appThemeTransparent()) {
                functionsClass.setThemeColorPreferences(activity?.findViewById(R.id.fullPreferencesActivity), activity?.findViewById(R.id.preferencesToolbar), true, getString(R.string.settingTitle), functionsClass.appVersionName(context?.packageName))
            } else {
                functionsClass.setThemeColorPreferences(activity?.findViewById(R.id.fullPreferencesActivity), activity?.findViewById(R.id.preferencesToolbar), false, getString(R.string.settingTitle), functionsClass.appVersionName(context?.packageName))
            }

            functionsClass.saveDefaultPreference("LitePreferences", false)

            false
        }

        blur.setOnPreferenceClickListener {
            if (functionsClass.appThemeTransparent()) {
                functionsClass.setThemeColorPreferences(activity?.findViewById(R.id.fullPreferencesActivity), activity?.findViewById(R.id.preferencesToolbar), true, getString(R.string.settingTitle), functionsClass.appVersionName(context?.packageName))
            } else {
                functionsClass.setThemeColorPreferences(activity?.findViewById(R.id.fullPreferencesActivity), activity?.findViewById(R.id.preferencesToolbar), false, getString(R.string.settingTitle), functionsClass.appVersionName(context?.packageName))
            }

            functionsClass.saveDefaultPreference("LitePreferences", false)

            false
        }

        support.setOnPreferenceClickListener {
            functionsClass.ContactSupport(activity)

            true
        }
    }

    override fun onStart() {
        super.onStart()

        val layerDrawableLoadLogo = context?.getDrawable(R.drawable.ic_launcher_layer) as LayerDrawable
        val gradientDrawableLoadLogo = layerDrawableLoadLogo.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable
        gradientDrawableLoadLogo.setTint(PublicVariable.primaryColorOpposite)
        whatsnew.icon = layerDrawableLoadLogo
        whatsnew.setOnPreferenceClickListener {
            functionsClass.ChangeLog(activity, betaChangeLog, betaVersionCode, true)

            true
        }

        val layerDrawableAdApp = context?.getDrawable(R.drawable.ic_ad_app_layer) as LayerDrawable
        val gradientDrawableAdApp = layerDrawableAdApp.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable
        gradientDrawableAdApp.setTint(PublicVariable.primaryColorOpposite)
        adApp.icon = layerDrawableAdApp
        adApp.title = Html.fromHtml(getString(R.string.adApp))
        adApp.summary = Html.fromHtml(getString(R.string.adAppSummary))
        adApp.setOnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_ad_app))))

            true
        }

        pinPassword.setOnPreferenceClickListener{
            if (functionsClass.securityServicesSubscribed()) {
                startActivity(Intent(context, HandlePinPassword::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
            } else {
                InAppBilling.ItemIAB = BillingManager.iapSecurityServices
                startActivity(Intent(context, InAppBilling::class.java)
                        .putExtra("UserEmailAddress", functionsClass.readPreference(".UserInformation", "userEmail", null))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
            }

            true
        }

        smart.setOnPreferenceChangeListener { preference, newValue ->
            if (!Settings.ACTION_USAGE_ACCESS_SETTINGS.isEmpty()) {
                if (sharedPreferences.getBoolean("smart", true)) {
                    smart.isChecked = true
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    startActivity(intent)

                    activity?.finish()
                } else if (!sharedPreferences.getBoolean("smart", true)) {
                    smart.isChecked = false
                    functionsClass.UsageAccess(activity, smart)
                }
            }
            true
        }

        observe.setOnPreferenceClickListener {
            if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(InteractionObserver::class.java)) {
                functionsClass.AccessibilityService(activity, observe)
            } else {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            true
        }

        boot.setOnPreferenceClickListener {
            val remoteOptions = resources.getStringArray(R.array.Boot)
            var alertDialogBuilder: AlertDialog.Builder? = null
            if (PublicVariable.themeLightDark === true) {
                alertDialogBuilder = AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light)
            } else if (PublicVariable.themeLightDark === false) {
                alertDialogBuilder = AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Dark)
            }
            alertDialogBuilder!!.setTitle(getString(R.string.boot))
            alertDialogBuilder.setSingleChoiceItems(remoteOptions, sharedPreferences.getString("boot", "1")!!.toInt(), null)
            alertDialogBuilder.setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                val editor = sharedPreferences.edit()
                val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition
                editor.putString("boot", selectedPosition.toString())
                editor.apply()

                val bootUpPreferences = sharedPreferences.getString("boot", "1")
                if (bootUpPreferences == "0") {
                    boot.summary = getString(R.string.boot_none)
                } else if (bootUpPreferences == "1") {
                    boot.summary = getString(R.string.shortcuts)
                } else if (bootUpPreferences == "2") {
                    boot.summary = getString(R.string.floatingCategory)
                } else if (bootUpPreferences == "3") {
                    boot.summary = getString(R.string.boot_warning)
                }
                functionsClass.addAppShortcuts()
            }
            if (functionsClass.returnAPI() > 22) {
                alertDialogBuilder.setNeutralButton(getString(R.string.read)) { dialog, which ->
                    functionsClass.RemoteRecovery(activity)
                }
            }
            alertDialogBuilder.show()

            true
        }

        notification.setOnPreferenceClickListener {
            if (functionsClass.NotificationAccess() && functionsClass.NotificationListenerRunning()) {
                val notification = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                notification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(notification)
            } else {
                functionsClass.NotificationAccessService(activity, notification)
            }

            true
        }

        shapes.setOnPreferenceClickListener {
            setupShapes(activity!!, sharedPreferences)

            true
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this@PreferencesFragment)

    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this@PreferencesFragment)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }
}