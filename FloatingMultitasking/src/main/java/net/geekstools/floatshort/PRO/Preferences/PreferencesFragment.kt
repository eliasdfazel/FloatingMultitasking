/*
 * Copyright © 2022 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/14/22, 7:20 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Preferences


import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityOptions
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.provider.Settings
import android.text.Html
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.PinPassword.PinPasswordConfigurations
import net.geekstools.floatshort.PRO.Utils.Functions.ApplicationThemeController
import net.geekstools.floatshort.PRO.Utils.Functions.Debug.Companion.PrintDebug
import net.geekstools.floatshort.PRO.Utils.Functions.Dialogues
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PopupApplicationShortcuts
import net.geekstools.floatshort.PRO.Utils.Functions.PreferencesIO
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver

class PreferencesFragment : PreferenceFragmentCompat() {

    lateinit var functionsClassLegacy: FunctionsClassLegacy
    lateinit var dialogues: Dialogues

    private val popupApplicationShortcuts: PopupApplicationShortcuts by lazy {
        PopupApplicationShortcuts(requireContext())
    }

    private val preferencesIO: PreferencesIO by lazy {
        PreferencesIO(requireContext())
    }

    private val applicationThemeController: ApplicationThemeController by lazy {
        ApplicationThemeController(requireContext())
    }

    private val fileIO: FileIO by lazy {
        FileIO(requireContext())
    }

    lateinit var sharedPreferences: SharedPreferences

    lateinit var themeColor: ListPreference
    lateinit var stick: ListPreference

    lateinit var stable: SwitchPreference
    lateinit var smart: SwitchPreference
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
    lateinit var privacy: Preference
    lateinit var whatsnew: Preference
    lateinit var adApp: Preference

    lateinit var runnablePressHold: Runnable
    val handlerPressHold = Handler(Looper.getMainLooper())

    var touchingDelay: Boolean = false
    var FromWidgetsConfigurations: Boolean = false
    var currentTheme: Boolean = false

    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    var betaChangeLog: String = "net.geekstools.floatshort.PRO"
    var betaVersionCode: String = "0"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_screen, rootKey)

        functionsClassLegacy = FunctionsClassLegacy(requireContext())
        dialogues = Dialogues(requireActivity(), functionsClassLegacy)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        stable = findPreference("stable")!!
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
        privacy = findPreference("privacy")!!

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
                getString(R.string.floatingFolders)
            }
            "3" -> {
                getString(R.string.boot_warning)
            }
            else -> {
                getString(R.string.boot_none)
            }
        }

        val appTheme = sharedPreferences.getString("themeColor", "2")
        if (appTheme == "1") {
            themeColor.summary = getString(R.string.light)
            PublicVariable.themeLightDark = true
        } else if (appTheme == "2") {
            themeColor.summary = getString(R.string.dark)
            PublicVariable.themeLightDark = false
        } else if (appTheme == "3") {
            functionsClassLegacy.checkLightDarkTheme()
            themeColor.summary = getString(R.string.dynamic)
        }

        delayPressHold.summary = functionsClassLegacy.readDefaultPreference("delayPressHold", 555).toString() + " " + getString(R.string.millis)

        stable.setOnPreferenceClickListener {
            if (sharedPreferences.getBoolean("stable", true)) {
                PublicVariable.Stable = true
                context?.startService(Intent(context, BindServices::class.java))
            } else if (!sharedPreferences.getBoolean("stable", true)) {
                PublicVariable.Stable = false
                if (PublicVariable.allFloatingCounter == 0) {
                    context?.stopService(Intent(context, BindServices::class.java))
                }
                functionsClassLegacy.saveDefaultPreference("LitePreferences", false)
            }
            false
        }

        themeColor.setOnPreferenceChangeListener { preference, newValue ->
            Handler(Looper.getMainLooper()).postDelayed({
                functionsClassLegacy.checkLightDarkTheme()

                when (newValue.toString()) {
                    "1" -> {
                        themeColor.summary = getString(R.string.light)

                        PublicVariable.forceReload = true
                        PublicVariable.themeLightDark = true

                        startActivity(Intent(context, PreferencesActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })

                        functionsClassLegacy.saveDefaultPreference("LitePreferences", false)
                    }
                    "2" -> {
                        themeColor.summary = getString(R.string.dark)

                        PublicVariable.forceReload = true
                        PublicVariable.themeLightDark = false

                        startActivity(Intent(context, PreferencesActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })

                        functionsClassLegacy.saveDefaultPreference("LitePreferences", false)
                    }
                    "3" -> {
                        themeColor.summary = getString(R.string.dynamic)

                        PublicVariable.forceReload = true
                        functionsClassLegacy.checkLightDarkTheme()

                        startActivity(Intent(context, PreferencesActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })

                        functionsClassLegacy.saveDefaultPreference("LitePreferences", false)
                    }
                }
            }, 357)

            true
        }

        stick.setOnPreferenceChangeListener { preference, newValue ->
            val sticky = sharedPreferences!!.getString("stick", "1")
            if (sticky == "1") {
                PublicVariable.forceReload = false
                stick.summary = getString(R.string.leftEdge)
            } else if (sticky == "2") {
                PublicVariable.forceReload = false
                stick.summary = getString(R.string.rightEdge)
            }

            true
        }

        support.setOnPreferenceClickListener {
            functionsClassLegacy.ContactSupport(activity)

            true
        }

        privacy.setOnPreferenceClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacyPolicy))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onStart() {
        super.onStart()

        val layerDrawableLoadLogo = context?.getDrawable(R.drawable.ic_launcher_layer) as LayerDrawable
        val gradientDrawableLoadLogo = layerDrawableLoadLogo.findDrawableByLayerId(R.id.backgroundTemporary) as BitmapDrawable
        gradientDrawableLoadLogo.setTint(PublicVariable.primaryColorOpposite)
        whatsnew.icon = layerDrawableLoadLogo
        whatsnew.setOnPreferenceClickListener {
            dialogues.changeLogPreference(betaChangeLog, betaVersionCode)

            true
        }

        val layerDrawableAdApp = context?.getDrawable(R.drawable.ic_ad_app_layer) as LayerDrawable
        val gradientDrawableAdApp = layerDrawableAdApp.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable
        gradientDrawableAdApp.setTint(PublicVariable.primaryColorOpposite)
        adApp.icon = layerDrawableAdApp
        adApp.title = Html.fromHtml(getString(R.string.adApp), Html.FROM_HTML_MODE_COMPACT)
        adApp.summary = Html.fromHtml(getString(R.string.adAppSummary), Html.FROM_HTML_MODE_COMPACT)
        adApp.setOnPreferenceClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_ad_app)))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

            true
        }

        pinPassword.setOnPreferenceClickListener{
            if (functionsClassLegacy.securityServicesSubscribed()) {
                startActivity(Intent(context, PinPasswordConfigurations::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
            } else {

                startActivity(Intent(requireContext(), InitializeInAppBilling::class.java).apply {
                    putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.SubscriptionPurchase)
                    putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemSecurityServices)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }, ActivityOptions.makeCustomAnimation(requireContext(), R.anim.down_up, android.R.anim.fade_out).toBundle())

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

                    functionsClassLegacy.UsageAccess(activity, smart)
                }
            }
            true
        }

        observe.setOnPreferenceClickListener {
            if (!functionsClassLegacy.AccessibilityServiceEnabled() && !functionsClassLegacy.SettingServiceRunning(InteractionObserver::class.java)) {
                functionsClassLegacy.AccessibilityServiceDialogue(activity, observe)
            } else {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            true
        }

        boot.setOnPreferenceClickListener {
            val remoteOptions = resources.getStringArray(R.array.Boot)
            val alertDialogBuilder: AlertDialog.Builder = if (PublicVariable.themeLightDark) {
                AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light)
            } else if (!PublicVariable.themeLightDark) {
                AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Dark)
            } else {
                AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light)
            }
            alertDialogBuilder .setTitle(getString(R.string.boot))
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
                    boot.summary = getString(R.string.floatingFolders)
                } else if (bootUpPreferences == "3") {
                    boot.summary = getString(R.string.boot_warning)
                }
                popupApplicationShortcuts.addPopupApplicationShortcuts()
            }
            if (functionsClassLegacy.returnAPI() > 22) {
                alertDialogBuilder.setNeutralButton(getString(R.string.read)) { dialog, which ->
                    functionsClassLegacy.RemoteRecovery(activity)
                }
            }
            alertDialogBuilder.show()

            true
        }

        notification.setOnPreferenceClickListener {

            if (functionsClassLegacy.NotificationAccess() && functionsClassLegacy.NotificationListenerRunning()) {

                val notification = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                notification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(notification)

            } else {

                functionsClassLegacy.NotificationAccessService(activity, notification)
            }

            true
        }

        shapes.setOnPreferenceClickListener {
            PreferencesDataUtilShape(requireActivity(), sharedPreferences, functionsClassLegacy, shapes).let {
                setupShapes(it)
            }

            true
        }

        freeForm.setOnPreferenceClickListener{
            if (functionsClassLegacy.FreeForm()) {
                functionsClassLegacy.FreeFormInformation(activity, freeForm)
            } else {
                freeForm.isChecked = false
            }

            true
        }

        autotrans.setOnPreferenceClickListener {
            val layoutParams = WindowManager.LayoutParams()
            val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics).toInt()
            val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 270f, resources.displayMetrics).toInt()

            layoutParams.width = dialogueWidth
            layoutParams.height = dialogueHeight
            layoutParams.windowAnimations = android.R.style.Animation_Dialog
            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            layoutParams.dimAmount = 0.57f

            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.seekbar_preferences)
            dialog.window!!.attributes = layoutParams
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
            dialog.setCancelable(true)

            val seekBarView: View = dialog.findViewById<RelativeLayout>(R.id.seekBarView)
            seekBarView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

            val transparentIcon = dialog.findViewById<ImageView>(R.id.preferenceIcon)
            val seekBarPreferences = dialog.findViewById<SeekBar>(R.id.seekBarPreferences)
            val dialogueTitle = dialog.findViewById<TextView>(R.id.dialogueTitle)
            val revertDefault = dialog.findViewById<TextView>(R.id.revertDefault)

            seekBarPreferences.thumbTintList = ColorStateList.valueOf(PublicVariable.primaryColor)
            seekBarPreferences.thumbTintMode = PorterDuff.Mode.SRC_IN
            seekBarPreferences.progressTintList = ColorStateList.valueOf(PublicVariable.primaryColorOpposite)
            seekBarPreferences.progressTintMode = PorterDuff.Mode.SRC_IN

            seekBarPreferences.max = 213
            seekBarPreferences.progress = functionsClassLegacy.readDefaultPreference("autoTransProgress", 0)

            var layerDrawableLoadLogo: Drawable?
            try {
                val backgroundDot = functionsClassLegacy.shapesDrawables().mutate()
                backgroundDot.setTint(PublicVariable.primaryColorOpposite)
                layerDrawableLoadLogo = LayerDrawable(arrayOf(
                        backgroundDot,
                        requireContext().getDrawable(R.drawable.ic_launcher_dots)
                ))
            } catch (e: NullPointerException) {
                e.printStackTrace()
                layerDrawableLoadLogo = requireContext().getDrawable(R.drawable.ic_launcher)
            }

            transparentIcon.imageAlpha = functionsClassLegacy.readDefaultPreference("autoTrans", 255)
            transparentIcon.setImageDrawable(layerDrawableLoadLogo)

            dialogueTitle.text = Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + getString(R.string.autotrans) + "</font>", Html.FROM_HTML_MODE_COMPACT)
            dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
            revertDefault.setTextColor(PublicVariable.colorLightDarkOpposite)

            seekBarPreferences.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val alpha = 255 - progress
                    transparentIcon.imageAlpha = alpha
                    functionsClassLegacy.saveDefaultPreference("autoTrans", alpha)
                    functionsClassLegacy.saveDefaultPreference("autoTransProgress", progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })

            revertDefault.setOnClickListener {
                functionsClassLegacy.saveDefaultPreference("autoTrans", 113)
                functionsClassLegacy.saveDefaultPreference("autoTransProgress", 95)
                transparentIcon.imageAlpha = 113
                seekBarPreferences.progress = 95
            }

            dialog.setOnDismissListener {
                val drawPrefAutoTrans = requireContext().getDrawable(R.drawable.draw_pref)!!.mutate() as LayerDrawable
                val backPrefAutoTrans = drawPrefAutoTrans.findDrawableByLayerId(R.id.backgroundTemporary).mutate()
                backPrefAutoTrans.setTint(PublicVariable.primaryColor)
                backPrefAutoTrans.alpha = functionsClassLegacy.readDefaultPreference("autoTrans", 255)
                autotrans.icon = drawPrefAutoTrans

                PublicVariable.forceReload = false

                dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            dialog.show()

            true
        }

        sizes.setOnPreferenceClickListener {
            val layoutParams = WindowManager.LayoutParams()
            val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics).toInt()
            val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 270f, resources.displayMetrics).toInt()

            layoutParams.width = dialogueWidth
            layoutParams.height = dialogueHeight
            layoutParams.windowAnimations = android.R.style.Animation_Dialog
            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            layoutParams.dimAmount = 0.57f

            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.seekbar_preferences)
            dialog.window!!.attributes = layoutParams
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
            dialog.setCancelable(true)

            val seekBarView: View = dialog.findViewById<RelativeLayout>(R.id.seekBarView)
            seekBarView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

            val transparentIcon = dialog.findViewById<ImageView>(R.id.preferenceIcon)
            val seekBarPreferences = dialog.findViewById<SeekBar>(R.id.seekBarPreferences)
            val dialogueTitle = dialog.findViewById<TextView>(R.id.dialogueTitle)
            val revertDefault = dialog.findViewById<TextView>(R.id.revertDefault)


            seekBarPreferences.thumbTintList = ColorStateList.valueOf(PublicVariable.primaryColor)
            seekBarPreferences.thumbTintMode = PorterDuff.Mode.SRC_IN
            seekBarPreferences.progressTintList = ColorStateList.valueOf(PublicVariable.primaryColorOpposite)
            seekBarPreferences.progressTintMode = PorterDuff.Mode.SRC_IN

            seekBarPreferences.max = 5
            seekBarPreferences.progress = functionsClassLegacy.readDefaultPreference("floatingSizeProgress", 2)

            var layerDrawableLoadLogo: Drawable?
            try {
                val backgroundDot = functionsClassLegacy.shapesDrawables().mutate()
                backgroundDot.setTint(PublicVariable.primaryColorOpposite)
                layerDrawableLoadLogo = LayerDrawable(arrayOf(
                        backgroundDot,
                        requireContext().getDrawable(R.drawable.ic_launcher_dots)
                ))
            } catch (e: java.lang.NullPointerException) {
                e.printStackTrace()
                layerDrawableLoadLogo = requireContext().getDrawable(R.drawable.ic_launcher)
            }

            val iconHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, functionsClassLegacy.readDefaultPreference("floatingSize", 39).toFloat(), resources.displayMetrics).toInt()
            val layoutParamsIcon = RelativeLayout.LayoutParams(
                    iconHW,
                    iconHW
            )
            layoutParamsIcon.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.seekBarView)
            layoutParamsIcon.addRule(RelativeLayout.BELOW, R.id.extraInfo)
            transparentIcon.layoutParams = layoutParamsIcon
            transparentIcon.setImageDrawable(layerDrawableLoadLogo)

            dialogueTitle.text = Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + getString(R.string.shortsizepref) + "</font>", Html.FROM_HTML_MODE_COMPACT)
            dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
            revertDefault.setTextColor(PublicVariable.colorLightDarkOpposite)

            val progressTemp = intArrayOf(1, 2, 3, 4, 5, 6)
            seekBarPreferences.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val size = 13 * progressTemp[progress]
                    functionsClassLegacy.saveDefaultPreference("floatingSize", size)
                    functionsClassLegacy.saveDefaultPreference("floatingSizeProgress", progress)
                    val iconHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size.toFloat(), resources.displayMetrics).toInt()
                    val layoutParamsIcon = RelativeLayout.LayoutParams(
                            iconHW,
                            iconHW
                    )
                    layoutParamsIcon.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.seekBarView)
                    layoutParamsIcon.addRule(RelativeLayout.BELOW, R.id.extraInfo)
                    transparentIcon.layoutParams = layoutParamsIcon
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })

            revertDefault.setOnClickListener {
                functionsClassLegacy.saveDefaultPreference("floatingSize", 39)
                functionsClassLegacy.saveDefaultPreference("floatingSizeProgress", 2)

                val iconHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 39f, resources.displayMetrics).toInt()
                val layoutParamsIcon = RelativeLayout.LayoutParams(
                        iconHW,
                        iconHW
                )
                layoutParamsIcon.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.seekBarView)
                layoutParamsIcon.addRule(RelativeLayout.BELOW, R.id.extraInfo)

                transparentIcon.layoutParams = layoutParamsIcon
                seekBarPreferences.progress = 2
            }

            dialog.setOnDismissListener {
                PublicVariable.forceReload = false

                dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            dialog.show()

            true
        }

        delayPressHold.setOnPreferenceClickListener {
            val layoutParams = WindowManager.LayoutParams()
            val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics).toInt()
            val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 270f, resources.displayMetrics).toInt()

            layoutParams.width = dialogueWidth
            layoutParams.height = dialogueHeight
            layoutParams.windowAnimations = android.R.style.Animation_Dialog
            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            layoutParams.dimAmount = 0.57f

            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.seekbar_preferences)
            dialog.window!!.attributes = layoutParams
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
            dialog.setCancelable(true)

            val seekBarView: View = dialog.findViewById<View>(R.id.seekBarView) as RelativeLayout
            seekBarView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

            val extraInfo = dialog.findViewById<TextView>(R.id.extraInfo)
            val delayIcon = dialog.findViewById<ImageView>(R.id.preferenceIcon)
            val seekBarPreferences = dialog.findViewById<SeekBar>(R.id.seekBarPreferences)
            val dialogueTitle = dialog.findViewById<TextView>(R.id.dialogueTitle)
            val revertDefault = dialog.findViewById<TextView>(R.id.revertDefault)

            extraInfo.visibility = View.VISIBLE
            extraInfo.setTextColor(PublicVariable.colorLightDarkOpposite)
            extraInfo.text = getString(R.string.delayPressHoldExtraInfo)

            seekBarPreferences.thumbTintList = ColorStateList.valueOf(PublicVariable.primaryColor)
            seekBarPreferences.thumbTintMode = PorterDuff.Mode.SRC_IN
            seekBarPreferences.progressTintList = ColorStateList.valueOf(PublicVariable.primaryColorOpposite)
            seekBarPreferences.progressTintMode = PorterDuff.Mode.SRC_IN

            seekBarPreferences.max = 1000
            seekBarPreferences.progress = functionsClassLegacy.readDefaultPreference("delayPressHold", 555)

            val layerDrawableLoadLogo: Drawable? = try {
                val backgroundDot = functionsClassLegacy.shapesDrawables().mutate()
                backgroundDot.setTint(PublicVariable.primaryColorOpposite)

                LayerDrawable(arrayOf(
                        backgroundDot,
                        requireContext().getDrawable(R.drawable.ic_launcher_dots)
                ))

                layerDrawableLoadLogo

            } catch (e: java.lang.NullPointerException) {
                e.printStackTrace()

                requireContext().getDrawable(R.drawable.ic_launcher)

            }


            delayIcon.setImageDrawable(layerDrawableLoadLogo)

            dialogueTitle.text = Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + getString(R.string.delayPressHold) + "</font>", Html.FROM_HTML_MODE_COMPACT)
            dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
            revertDefault.setTextColor(PublicVariable.colorLightDarkOpposite)

            delayIcon.setOnClickListener { }
            delayIcon.onFocusChangeListener = OnFocusChangeListener { view, hasFocus -> }
            delayIcon.setOnTouchListener { view, motionEvent ->

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touchingDelay = true
                        runnablePressHold = Runnable {
                            if (touchingDelay) {
                                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                vibrator.vibrate(333)

                                PrintDebug("*** millis delay ::: " + functionsClassLegacy.readDefaultPreference("delayPressHold", 555))
                            }
                        }
                        handlerPressHold.postDelayed(runnablePressHold, functionsClassLegacy.readDefaultPreference("delayPressHold", 555).toLong())
                    }
                    MotionEvent.ACTION_UP -> {
                        touchingDelay = false
                        handlerPressHold.removeCallbacks(runnablePressHold)
                    }
                }

                false
            }

            seekBarPreferences.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val delay = 333 + progress
                    functionsClassLegacy.saveDefaultPreference("delayPressHold", delay)
                    functionsClassLegacy.saveDefaultPreference("delayPressHoldProgress", progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })

            revertDefault.setOnClickListener {
                functionsClassLegacy.saveDefaultPreference("delayPressHold", 555)
                functionsClassLegacy.saveDefaultPreference("delayPressHoldProgress", 0)
                seekBarPreferences.progress = 555
            }

            dialog.setOnDismissListener {
                delayPressHold.summary = functionsClassLegacy.readDefaultPreference("delayPressHold", 555).toString() + " " + getString(R.string.millis)
                PublicVariable.forceReload = false
                dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            dialog.show()

            true
        }

        flingSensitivity.setOnPreferenceClickListener {
            PreferencesDataUtilFling(requireActivity(), functionsClassLegacy).let {
                setupFlingSensitivity(it)
            }

            true
        }

        if (!requireContext().getFileStreamPath(".LitePreferenceCheckpoint").exists()) {
            val activityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            val memoryInfo = ActivityManager.MemoryInfo()
            if (activityManager != null) {
                activityManager.getMemoryInfo(memoryInfo)
                if (memoryInfo.totalMem <= 2000000000 || memoryInfo.lowMemory) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        listView.smoothScrollToPosition(listView.bottom)

                        Handler(Looper.getMainLooper()).postDelayed({
                            functionsClassLegacy.litePreferenceConfirm(activity)

                            fileIO.saveFileEmpty(".LitePreferenceCheckpoint")
                        }, 555)
                    }, 333)
                }
            }
        }
        lite.setOnPreferenceClickListener {
            functionsClassLegacy.litePreferenceConfirm(activity)

            true
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.fetch(0).addOnSuccessListener {

            firebaseRemoteConfig.activate().addOnSuccessListener {

                if (!this@PreferencesFragment.isRemoving) {

                    Glide.with(requireContext())
                        .load(firebaseRemoteConfig.getString(getString(R.string.adAppIconLink)))
                        .transform(RoundedCorners(functionsClassLegacy.DpToInteger(99)))
                        .addListener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {

                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                                requireActivity().runOnUiThread {
                                    adApp.icon = resource
                                }

                                return false
                            }
                        }).submit()
                    adApp.title = Html.fromHtml(firebaseRemoteConfig.getString(getString(R.string.adAppTitle)), Html.FROM_HTML_MODE_COMPACT)
                    adApp.summary = Html.fromHtml(firebaseRemoteConfig.getString(getString(R.string.adAppSummaries)), Html.FROM_HTML_MODE_COMPACT)

                    adApp.setOnPreferenceClickListener {

                        startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse(firebaseRemoteConfig.getString(getString(R.string.adAppLink)))))

                        true
                    }

                }

            }
        }
    }

    override fun onResume() {
        super.onResume()

        val drawSecurity: LayerDrawable = requireContext().getDrawable(R.drawable.draw_security_preferences) as LayerDrawable
        val backSecurity = drawSecurity.findDrawableByLayerId(R.id.backgroundTemporary)
        backSecurity.setTint(PublicVariable.primaryColorOpposite)
        pinPassword.icon = drawSecurity

        val drawSmart: LayerDrawable = requireContext().getDrawable(R.drawable.draw_smart) as LayerDrawable
        val backSmart = drawSmart.findDrawableByLayerId(R.id.backgroundTemporary)

        val drawPref: LayerDrawable = requireContext().getDrawable(R.drawable.draw_pref) as LayerDrawable
        val backPref = drawPref.findDrawableByLayerId(R.id.backgroundTemporary)

        val drawPrefAutoTrans: LayerDrawable = requireContext().getDrawable(R.drawable.draw_pref)!!.mutate() as LayerDrawable
        val backPrefAutoTrans = drawPrefAutoTrans.findDrawableByLayerId(R.id.backgroundTemporary).mutate()

        val drawPrefLite: LayerDrawable = requireContext().getDrawable(R.drawable.draw_pref)!!.mutate() as LayerDrawable
        val backPrefLite = drawPrefLite.findDrawableByLayerId(R.id.backgroundTemporary).mutate()
        val drawablePrefLite = drawPrefLite.findDrawableByLayerId(R.id.wPref)
        backPrefLite.setTint(if (PublicVariable.themeLightDark) requireContext().getColor(R.color.dark) else requireContext().getColor(R.color.light))
        drawablePrefLite.setTint(if (PublicVariable.themeLightDark) requireContext().getColor(R.color.light) else requireContext().getColor(R.color.dark))
        lite.icon = drawPrefLite

        val drawFloatIt: LayerDrawable = requireContext().getDrawable(R.drawable.draw_floatit) as LayerDrawable
        val backFloatIt = drawFloatIt.findDrawableByLayerId(R.id.backgroundTemporary)

        val drawSupport: LayerDrawable = requireContext().getDrawable(R.drawable.draw_support) as LayerDrawable
        val backSupport = drawSupport.findDrawableByLayerId(R.id.backgroundTemporary)

        backSmart.setTint(PublicVariable.primaryColor)
        backPref.setTint(PublicVariable.primaryColor)

        backPrefAutoTrans.setTint(PublicVariable.primaryColor)
        backPrefAutoTrans.alpha = functionsClassLegacy.readDefaultPreference("autoTrans", 255)

        backFloatIt.setTint(PublicVariable.primaryColor)
        backSupport.setTint(PublicVariable.primaryColorOpposite)

        stable.icon = drawPref
        autotrans.icon = drawPrefAutoTrans
        floatingSplash.icon = drawPref
        themeColor.icon = drawPref
        sizes.icon = drawPref
        delayPressHold.icon = drawPref
        flingSensitivity.icon = drawPref
        stick.icon = drawPref
        notification.icon = drawPref

        smart.icon = drawSmart
        observe.icon = drawSmart
        boot.icon = drawSmart
        freeForm.icon = drawFloatIt

        support.icon = drawSupport

        when (sharedPreferences.getInt("iconShape", 0)) {
            1 -> {
                val drawableTeardrop: Drawable = requireContext().getDrawable(R.drawable.droplet_icon)!!
                drawableTeardrop.setTint(PublicVariable.primaryColor)
                val layerDrawable1 = LayerDrawable(arrayOf(drawableTeardrop, requireContext().getDrawable(R.drawable.w_pref_gui)))
                shapes.icon = layerDrawable1
                shapes.summary = getString(R.string.droplet)
            }
            2 -> {
                val drawableCircle: Drawable = requireContext().getDrawable(R.drawable.circle_icon)!!
                drawableCircle.setTint(PublicVariable.primaryColor)
                val layerDrawable2 = LayerDrawable(arrayOf(drawableCircle, requireContext().getDrawable(R.drawable.w_pref_gui)))
                shapes.icon = layerDrawable2
                shapes.summary = getString(R.string.circle)
            }
            3 -> {
                val drawableSquare: Drawable = requireContext().getDrawable(R.drawable.square_icon)!!
                drawableSquare.setTint(PublicVariable.primaryColor)
                val layerDrawable3 = LayerDrawable(arrayOf(drawableSquare, requireContext().getDrawable(R.drawable.w_pref_gui)))
                shapes.icon = layerDrawable3
                shapes.summary = getString(R.string.square)
            }
            4 -> {
                val drawableSquircle: Drawable = requireContext().getDrawable(R.drawable.squircle_icon)!!
                drawableSquircle.setTint(PublicVariable.primaryColor)
                val layerDrawable4 = LayerDrawable(arrayOf(drawableSquircle, requireContext().getDrawable(R.drawable.w_pref_gui)))
                shapes.icon = layerDrawable4
                shapes.summary = getString(R.string.squircle)
            }
            0 -> {
                val drawableNoShape: Drawable = requireContext().getDrawable(R.drawable.w_pref_noshape)!!
                drawableNoShape.setTint(PublicVariable.primaryColor)
                shapes.icon = drawableNoShape
            }
        }

        if (functionsClassLegacy.customIconsEnable()) {
            shapes.icon = functionsClassLegacy.applicationIcon(functionsClassLegacy.customIconPackageName())
            shapes.summary = functionsClassLegacy.applicationName(functionsClassLegacy.customIconPackageName())
        }

        if (functionsClassLegacy.UsageStatsEnabled()) {
            PublicVariable.forceReload = true

            smart.isChecked = true
        } else {
            smart.isChecked = false
        }

        if (functionsClassLegacy.returnAPI() < 24) {
            observe.summary = getString(R.string.observeSum)
            observe.isEnabled = false
            freeForm.summary = getString(R.string.observeSum)
            freeForm.isEnabled = false
        }

        observe.isChecked = functionsClassLegacy.AccessibilityServiceEnabled() && functionsClassLegacy.SettingServiceRunning(InteractionObserver::class.java)

        notification.isChecked = functionsClassLegacy.NotificationAccess() && functionsClassLegacy.NotificationListenerRunning()

        freeForm.isChecked = functionsClassLegacy.freeFormSupport(context) && functionsClassLegacy.FreeForm()
    }

    override fun onPause() {
        super.onPause()

        functionsClassLegacy.loadSavedColor()

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}