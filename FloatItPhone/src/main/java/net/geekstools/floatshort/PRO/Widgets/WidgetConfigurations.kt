/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 6:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widgets

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.withIndex
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.Preferences.PreferencesActivity
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchResultType
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.PinPassword.PinPasswordConfigurations
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Shortcuts.ApplicationsViewPhone
import net.geekstools.floatshort.PRO.Utils.AdapterDataItem.RecycleViewSmoothLayoutGrid
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.Debug
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryFolders
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryShortcuts
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryWidgets
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.SwipeGestureListener
import net.geekstools.floatshort.PRO.Utils.UI.PopupIndexedFastScroller.Factory.IndexedFastScrollerFactory
import net.geekstools.floatshort.PRO.Utils.UI.PopupIndexedFastScroller.IndexedFastScroller
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataModel
import net.geekstools.floatshort.PRO.Widgets.WidgetsAdapter.ConfiguredWidgetsAdapter
import net.geekstools.floatshort.PRO.Widgets.WidgetsAdapter.InstalledWidgetsAdapter
import net.geekstools.floatshort.PRO.Widgets.WidgetsAdapter.WidgetSectionedConfiguredAdapter
import net.geekstools.floatshort.PRO.Widgets.WidgetsAdapter.WidgetSectionedInstalledAdapter
import net.geekstools.floatshort.PRO.databinding.WidgetConfigurationsViewsBinding
import java.util.*
import kotlin.math.hypot
import kotlin.math.roundToInt

class WidgetConfigurations : AppCompatActivity(), GestureListenerInterface {

    private val widgetConfigurationsDependencyInjection: WidgetConfigurationsDependencyInjection by lazy {
        WidgetConfigurationsDependencyInjection(applicationContext)
    }

    private val indexListConfigured: ArrayList<String> = ArrayList<String>()
    private val indexListInstalled: ArrayList<String> = ArrayList<String>()

    private val installedWidgetsSections: ArrayList<WidgetSectionedInstalledAdapter.Section> = ArrayList<WidgetSectionedInstalledAdapter.Section>()
    private val configuredWidgetsSections: ArrayList<WidgetSectionedConfiguredAdapter.Section> = ArrayList<WidgetSectionedConfiguredAdapter.Section>()

    private lateinit var configuredWidgetsRecyclerViewAdapter: RecyclerView.Adapter<ConfiguredWidgetsAdapter.ViewHolder>
    private lateinit var installedWidgetsRecyclerViewAdapter: RecyclerView.Adapter<InstalledWidgetsAdapter.ViewHolder>

    private lateinit var widgetSectionedConfiguredAdapter: WidgetSectionedConfiguredAdapter
    private lateinit var widgetSectionedInstalledAdapter: WidgetSectionedInstalledAdapter

    private lateinit var installedWidgetsRecyclerViewLayoutManager: GridLayoutManager
    private lateinit var configuredWidgetsRecyclerViewLayoutManager: GridLayoutManager

    private lateinit var widgetProviderInfoList: ArrayList<AppWidgetProviderInfo>
    private val installedWidgetsAdapterItems: ArrayList<AdapterItems> = ArrayList<AdapterItems>()
    private val configuredWidgetsAdapterItems: ArrayList<AdapterItems> = ArrayList<AdapterItems>()

    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var appWidgetHost: AppWidgetHost

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, widgetConfigurationsDependencyInjection.functionsClassLegacy.customIconPackageName())
    }

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(applicationContext, this@WidgetConfigurations)
    }

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    private var installedWidgetsLoaded: Boolean = false
    private var configuredWidgetAvailable: Boolean = false

    private lateinit var firebaseAuth: FirebaseAuth

    companion object {
        var alreadyAuthenticatedWidgets = false
    }

    private lateinit var widgetConfigurationsViewsBinding: WidgetConfigurationsViewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetConfigurationsViewsBinding = WidgetConfigurationsViewsBinding.inflate(layoutInflater)
        setContentView(widgetConfigurationsViewsBinding.root)

        widgetConfigurationsDependencyInjection.functionsClassLegacy.loadSavedColor()
        widgetConfigurationsDependencyInjection.functionsClassLegacy.checkLightDarkTheme()

        if (!widgetConfigurationsDependencyInjection.functionsClassLegacy.readPreference("WidgetsInformation", "Reallocated", true)
                && getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
            startActivity(Intent(applicationContext, WidgetsReallocationProcess::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
            finish()
            return
        }

        widgetConfigurationsViewsBinding.widgetPickerTitle.text = Html.fromHtml(getString(net.geekstools.floatshort.PRO.R.string.widgetPickerTitle))
        widgetConfigurationsViewsBinding.widgetPickerTitle.setTextColor(if (PublicVariable.themeLightDark) getColor(R.color.dark) else getColor(net.geekstools.floatshort.PRO.R.color.light))

        installedWidgetsRecyclerViewLayoutManager = RecycleViewSmoothLayoutGrid(applicationContext, widgetConfigurationsDependencyInjection.functionsClassLegacy.columnCount(190), OrientationHelper.VERTICAL, false)
        widgetConfigurationsViewsBinding.installedWidgetList.layoutManager = installedWidgetsRecyclerViewLayoutManager

        configuredWidgetsRecyclerViewLayoutManager = RecycleViewSmoothLayoutGrid(applicationContext, widgetConfigurationsDependencyInjection.functionsClassLegacy.columnCount(190), OrientationHelper.VERTICAL, false)
        widgetConfigurationsViewsBinding.configuredWidgetList.layoutManager = configuredWidgetsRecyclerViewLayoutManager

        widgetConfigurationsDependencyInjection.applicationThemeController.setThemeColorFloating(this, widgetConfigurationsViewsBinding.MainView, widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent())

        appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        appWidgetHost = AppWidgetHost(applicationContext, System.currentTimeMillis().toInt())

        if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {

            loadConfiguredWidgets().invokeOnCompletion {

            }
        } else {
            widgetConfigurationsViewsBinding.reconfigure.visibility = View.INVISIBLE

            widgetConfigurationsViewsBinding.actionButton.bringToFront()
            widgetConfigurationsViewsBinding.addWidget.bringToFront()

            widgetConfigurationsViewsBinding.addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener)

            if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) {
                widgetConfigurationsViewsBinding.loadingSplash.setBackgroundColor(Color.TRANSPARENT)
            } else {
                widgetConfigurationsViewsBinding.loadingSplash.setBackgroundColor(window.navigationBarColor)
            }

            val typeface = Typeface.createFromAsset(assets, "upcil.ttf")
            widgetConfigurationsViewsBinding.loadingText.setTypeface(typeface)

            if (PublicVariable.themeLightDark) {
                widgetConfigurationsViewsBinding.loadingProgress.indeterminateDrawable.setTint(PublicVariable.darkMutedColor)
                widgetConfigurationsViewsBinding.loadingText.setTextColor(getColor(R.color.dark))
            } else if (!PublicVariable.themeLightDark) {
                widgetConfigurationsViewsBinding.loadingProgress.indeterminateDrawable.setTint(PublicVariable.vibrantColor)
                widgetConfigurationsViewsBinding.loadingText.setTextColor(getColor(R.color.light))
            }

            widgetConfigurationsViewsBinding.switchFloating.bringToFront()
        }

        val drawAddWidget = getDrawable(R.drawable.draw_pref_add_widget) as LayerDrawable
        val backAddWidget = drawAddWidget.findDrawableByLayerId(R.id.backgroundTemporary)
        val frontAddWidget = drawAddWidget.findDrawableByLayerId(R.id.frontTemporary).mutate()
        backAddWidget.setTint(getColor(R.color.default_color_game))
        frontAddWidget.setTint(getColor(R.color.light))
        widgetConfigurationsViewsBinding.addWidget.setImageDrawable(drawAddWidget)

        val drawPreferenceAction = getDrawable(R.drawable.draw_pref_action) as LayerDrawable
        val backPreferenceAction = drawPreferenceAction.findDrawableByLayerId(R.id.backgroundTemporary)
        backPreferenceAction.setTint(PublicVariable.primaryColorOpposite)
        widgetConfigurationsViewsBinding.actionButton.setImageDrawable(drawPreferenceAction)

        widgetConfigurationsViewsBinding.switchApps.setTextColor(getColor(R.color.light))
        widgetConfigurationsViewsBinding.switchCategories.setTextColor(getColor(R.color.light))
        if (PublicVariable.themeLightDark /*light*/ && widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent() /*transparent*/) {
            widgetConfigurationsViewsBinding.switchApps.setTextColor(getColor(R.color.dark))
            widgetConfigurationsViewsBinding.switchCategories.setTextColor(getColor(R.color.dark))
        }

        widgetConfigurationsViewsBinding.switchCategories.setBackgroundColor(if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) widgetConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        widgetConfigurationsViewsBinding.switchCategories.setRippleColor(ColorStateList.valueOf(if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) widgetConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite))

        widgetConfigurationsViewsBinding.switchApps.setBackgroundColor(if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) widgetConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        widgetConfigurationsViewsBinding.switchApps.rippleColor = ColorStateList.valueOf(if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) widgetConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        widgetConfigurationsViewsBinding.recoveryAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        widgetConfigurationsViewsBinding.recoveryAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        val drawRecoverFloatingCategories = getDrawable(R.drawable.draw_recovery)?.mutate() as LayerDrawable?
        val backgroundRecoverFloatingCategories = drawRecoverFloatingCategories?.findDrawableByLayerId(R.id.backgroundTemporary)?.mutate()
        backgroundRecoverFloatingCategories?.setTint(if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) widgetConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        widgetConfigurationsViewsBinding.recoverFloatingCategories.setImageDrawable(drawRecoverFloatingCategories)
        widgetConfigurationsViewsBinding.recoverFloatingApps.setImageDrawable(drawRecoverFloatingCategories)

        widgetConfigurationsViewsBinding.actionButton.setOnClickListener {
            widgetConfigurationsDependencyInjection.functionsClassLegacy.doVibrate(33)

            if (!PublicVariable.actionCenter) {
                if (widgetConfigurationsViewsBinding.installedNestedScrollView.isShown) {

                    widgetConfigurationsViewsBinding.fastScrollerIndexIncludeInstalled.nestedIndexScrollView.visibility = View.INVISIBLE

                    if (!configuredWidgetAvailable) {
                        widgetConfigurationsViewsBinding.addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener)
                    }

                    ViewCompat.animate(widgetConfigurationsViewsBinding.addWidget)
                            .rotation(0.0f)
                            .withLayer()
                            .setDuration(300L)
                            .setInterpolator(OvershootInterpolator(3.0f))
                            .start()

                    val startRadius = 0
                    val endRadius = hypot(widgetConfigurationsDependencyInjection.functionsClassLegacy.displayX().toDouble(), widgetConfigurationsDependencyInjection.functionsClassLegacy.displayY().toDouble()).toInt()

                    val circularReveal = ViewAnimationUtils.createCircularReveal(widgetConfigurationsViewsBinding.installedNestedScrollView,
                            ((widgetConfigurationsViewsBinding.addWidget.x + widgetConfigurationsViewsBinding.addWidget.width / 2).roundToInt()),
                            ((widgetConfigurationsViewsBinding.addWidget.y + widgetConfigurationsViewsBinding.addWidget.height / 2).roundToInt()),
                            endRadius.toFloat(), startRadius.toFloat())
                    circularReveal.duration = 864
                    circularReveal.start()
                    circularReveal.addListener(object : Animator.AnimatorListener {

                        override fun onAnimationStart(animator: Animator) {

                        }

                        override fun onAnimationEnd(animator: Animator) {
                            widgetConfigurationsViewsBinding.installedNestedScrollView.visibility = View.INVISIBLE
                        }

                        override fun onAnimationCancel(animator: Animator) {

                        }

                        override fun onAnimationRepeat(animator: Animator) {

                        }
                    })

                    if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                        if (PublicVariable.themeLightDark) {
                            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                            }
                        }
                        val valueAnimator = ValueAnimator
                                .ofArgb(window.navigationBarColor, widgetConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(widgetConfigurationsDependencyInjection.functionsClassLegacy.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f))
                        valueAnimator.addUpdateListener { animator ->
                            window.statusBarColor = (animator.animatedValue) as Int
                            window.navigationBarColor = (animator.animatedValue) as Int
                        }
                        valueAnimator.start()
                    } else {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                        if (PublicVariable.themeLightDark) {
                            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                            }
                        }
                        val colorAnimation = ValueAnimator
                                .ofArgb(getWindow().navigationBarColor, PublicVariable.colorLightDark)
                        colorAnimation.addUpdateListener { animator ->
                            window.navigationBarColor = (animator.animatedValue) as Int
                            window.statusBarColor = (animator.animatedValue) as Int
                        }
                        colorAnimation.start()
                    }
                }

                val finalRadius = hypot(widgetConfigurationsDependencyInjection.functionsClassLegacy.displayX().toDouble(), widgetConfigurationsDependencyInjection.functionsClassLegacy.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(widgetConfigurationsViewsBinding.recoveryAction,
                        widgetConfigurationsViewsBinding.actionButton.x.roundToInt(),
                        widgetConfigurationsViewsBinding.actionButton.y.roundToInt(),
                        finalRadius.toFloat(), widgetConfigurationsDependencyInjection.functionsClassLegacy.DpToInteger(13).toFloat())
                circularReveal.duration = 777
                circularReveal.interpolator = AccelerateInterpolator()
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        widgetConfigurationsViewsBinding.recoveryAction.setVisibility(View.INVISIBLE)
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })

                widgetConfigurationsDependencyInjection.functionsClassLegacy.openActionMenuOption(this@WidgetConfigurations, widgetConfigurationsViewsBinding.fullActionViews,
                        widgetConfigurationsViewsBinding.actionButton,
                        widgetConfigurationsViewsBinding.fullActionViews.isShown)

            } else {
                widgetConfigurationsViewsBinding.recoveryAction.visibility = View.VISIBLE

                val finalRadius = hypot(widgetConfigurationsDependencyInjection.functionsClassLegacy.displayX().toDouble(), widgetConfigurationsDependencyInjection.functionsClassLegacy.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(widgetConfigurationsViewsBinding.recoveryAction,
                        widgetConfigurationsViewsBinding.actionButton.x.roundToInt(),
                        widgetConfigurationsViewsBinding.actionButton.y.roundToInt(),
                        widgetConfigurationsDependencyInjection.functionsClassLegacy.DpToInteger(13).toFloat(), finalRadius.toFloat())
                circularReveal.duration = 1300
                circularReveal.interpolator = AccelerateInterpolator()
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        widgetConfigurationsViewsBinding.recoveryAction.visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })

                widgetConfigurationsDependencyInjection.functionsClassLegacy.closeActionMenuOption(this@WidgetConfigurations, widgetConfigurationsViewsBinding.fullActionViews,
                        widgetConfigurationsViewsBinding.actionButton)
            }
        }
        widgetConfigurationsViewsBinding.switchCategories.setOnClickListener {

            widgetConfigurationsDependencyInjection.functionsClassLegacy.navigateToClass(this@WidgetConfigurations, FoldersConfigurations::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
        }
        widgetConfigurationsViewsBinding.switchApps.setOnClickListener {

            startActivity(Intent(applicationContext, ApplicationsViewPhone::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left).toBundle())
        }

        widgetConfigurationsViewsBinding.recoveryAction.setOnClickListener {

            Intent(applicationContext, RecoveryWidgets::class.java).apply {
                putExtra("AuthenticatedFloatIt", true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(this)
            }
        }
        widgetConfigurationsViewsBinding.recoverFloatingCategories.setOnClickListener {

            Intent(applicationContext, RecoveryFolders::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(this)
            }

            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
            widgetConfigurationsViewsBinding.recoverFloatingCategories.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    widgetConfigurationsViewsBinding.recoverFloatingCategories.setVisibility(View.INVISIBLE)
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }
        widgetConfigurationsViewsBinding.recoverFloatingApps.setOnClickListener {

            Intent(applicationContext, RecoveryShortcuts::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(this)
            }

            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
            widgetConfigurationsViewsBinding.recoverFloatingApps.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    widgetConfigurationsViewsBinding.recoverFloatingApps.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }

        widgetConfigurationsViewsBinding.actionButton.setOnLongClickListener {

            Intent().apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.setClass(this@WidgetConfigurations, PreferencesActivity::class.java)
                startActivity(this,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@WidgetConfigurations, widgetConfigurationsViewsBinding.actionButton, "transition").toBundle())
            }

            true
        }
        widgetConfigurationsViewsBinding.switchCategories.setOnLongClickListener {

            if (!widgetConfigurationsViewsBinding.recoverFloatingCategories.isShown) {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_show)
                widgetConfigurationsViewsBinding.recoverFloatingCategories.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        widgetConfigurationsViewsBinding.recoverFloatingCategories.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
                widgetConfigurationsViewsBinding.recoverFloatingCategories.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        widgetConfigurationsViewsBinding.recoverFloatingCategories.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }

            true
        }
        widgetConfigurationsViewsBinding.switchApps.setOnLongClickListener {

            if (!widgetConfigurationsViewsBinding.recoverFloatingApps.isShown) {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_show)
                widgetConfigurationsViewsBinding.recoverFloatingApps.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        widgetConfigurationsViewsBinding.recoverFloatingApps.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
                widgetConfigurationsViewsBinding.recoverFloatingApps.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        widgetConfigurationsViewsBinding.recoverFloatingApps.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }

            true
        }

        val drawFloatingLogo = getDrawable(R.drawable.draw_floating_widgets) as LayerDrawable
        val backFloatingLogo = drawFloatingLogo.findDrawableByLayerId(R.id.backgroundTemporary)
        backFloatingLogo.setTint(PublicVariable.primaryColorOpposite)
        widgetConfigurationsViewsBinding.loadingLogo.setImageDrawable(drawFloatingLogo)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        widgetConfigurationsViewsBinding.reconfigure.setOnClickListener {

            startActivity(Intent(applicationContext, WidgetsReallocationProcess::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

            this@WidgetConfigurations.finish()
        }

        widgetConfigurationsViewsBinding.addWidget.setOnClickListener {
            widgetConfigurationsDependencyInjection.functionsClassLegacy.doVibrate(177)

            if (widgetConfigurationsViewsBinding.installedNestedScrollView.isShown) {

                widgetConfigurationsViewsBinding.installedNestedScrollView.visibility = View.INVISIBLE
                widgetConfigurationsViewsBinding.fastScrollerIndexIncludeInstalled.nestedIndexScrollView.visibility = View.INVISIBLE

                if (!configuredWidgetAvailable) {
                    widgetConfigurationsViewsBinding.addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener)
                }

                ViewCompat.animate(widgetConfigurationsViewsBinding.addWidget)
                        .rotation(0.0f)
                        .withLayer()
                        .setDuration(300L)
                        .setInterpolator(OvershootInterpolator(3.0f))
                        .start()
                val xPosition = (widgetConfigurationsViewsBinding.addWidget.x + widgetConfigurationsViewsBinding.addWidget.width / 2).roundToInt()
                val yPosition = (widgetConfigurationsViewsBinding.addWidget.y + widgetConfigurationsViewsBinding.addWidget.height / 2).roundToInt()
                val startRadius = 0
                val endRadius = hypot(widgetConfigurationsDependencyInjection.functionsClassLegacy.displayX().toDouble(), widgetConfigurationsDependencyInjection.functionsClassLegacy.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(widgetConfigurationsViewsBinding.installedNestedScrollView,
                        xPosition, yPosition,
                        endRadius.toFloat(), startRadius.toFloat())
                circularReveal.duration = 864
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animator: Animator) {

                    }

                    override fun onAnimationEnd(animator: Animator) {
                        widgetConfigurationsViewsBinding.installedNestedScrollView.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(animator: Animator) {

                    }

                    override fun onAnimationRepeat(animator: Animator) {

                    }
                })

                if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    if (PublicVariable.themeLightDark) {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                        }
                    }
                    val valueAnimator = ValueAnimator
                            .ofArgb(window.navigationBarColor, widgetConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(widgetConfigurationsDependencyInjection.functionsClassLegacy.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f))
                    valueAnimator.addUpdateListener { animator ->
                        window.statusBarColor = (animator.animatedValue) as Int
                        window.navigationBarColor = (animator.animatedValue) as Int
                    }
                    valueAnimator.start()
                } else {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    if (PublicVariable.themeLightDark) {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                        }
                    }
                    val colorAnimation = ValueAnimator
                            .ofArgb(window.navigationBarColor, PublicVariable.colorLightDark)
                    colorAnimation.addUpdateListener { animator ->
                        window.navigationBarColor = (animator.animatedValue) as Int
                        window.statusBarColor = (animator.animatedValue) as Int
                    }
                    colorAnimation.start()
                }
            } else {
                if (PublicVariable.actionCenter) {
                    widgetConfigurationsViewsBinding.recoveryAction.visibility = View.VISIBLE

                    val finalRadius = hypot(widgetConfigurationsDependencyInjection.functionsClassLegacy.displayX().toDouble(), widgetConfigurationsDependencyInjection.functionsClassLegacy.displayY().toDouble()).toInt()
                    val circularReveal = ViewAnimationUtils.createCircularReveal(widgetConfigurationsViewsBinding.recoveryAction,
                            widgetConfigurationsViewsBinding.actionButton.x.roundToInt(),
                            widgetConfigurationsViewsBinding.actionButton.y.roundToInt(),
                            widgetConfigurationsDependencyInjection.functionsClassLegacy.DpToInteger(13).toFloat(), finalRadius.toFloat())
                    circularReveal.duration = 1300
                    circularReveal.interpolator = AccelerateInterpolator()
                    circularReveal.start()
                    circularReveal.addListener(object : Animator.AnimatorListener {

                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            widgetConfigurationsViewsBinding.recoveryAction.visibility = View.VISIBLE
                        }

                        override fun onAnimationCancel(animation: Animator) {

                        }

                        override fun onAnimationRepeat(animation: Animator) {

                        }
                    })
                    widgetConfigurationsDependencyInjection.functionsClassLegacy.closeActionMenuOption(this@WidgetConfigurations, widgetConfigurationsViewsBinding.fullActionViews, widgetConfigurationsViewsBinding.actionButton)
                }

                loadInstalledWidgets()
            }
        }

        widgetConfigurationsViewsBinding.addWidget.setOnLongClickListener {

            val appWidgetId = appWidgetHost.allocateAppWidgetId()
            val appWidgetProviderInfos = ArrayList<AppWidgetProviderInfo>()
            val bundleArrayList = ArrayList<Bundle>()

            val pickIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK)
            pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, appWidgetProviderInfos)
            pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, bundleArrayList)
            startActivityForResult(pickIntent, InstalledWidgetsAdapter.SYSTEM_WIDGET_PICKER)

            true
        }
    }

    override fun onResume() {
        super.onResume()

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {
                            if (firebaseRemoteConfig.getLong(widgetConfigurationsDependencyInjection.functionsClassLegacy.versionCodeRemoteConfigKey()) > widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationVersionCode(packageName)) {
                                widgetConfigurationsDependencyInjection.functionsClassLegacy.notificationCreator(
                                        getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(widgetConfigurationsDependencyInjection.functionsClassLegacy.upcomingChangeLogSummaryConfigKey()),
                                        firebaseRemoteConfig.getLong(widgetConfigurationsDependencyInjection.functionsClassLegacy.versionCodeRemoteConfigKey()).toInt()
                                )
                            } else {

                            }
                        }
                    } else {

                    }
                }

        if (widgetConfigurationsDependencyInjection.functionsClassLegacy.readPreference(".Password", "Pin", "0") == "0" && widgetConfigurationsDependencyInjection.functionsClassLegacy.securityServicesSubscribed()) {

            startActivity(Intent(applicationContext, PinPasswordConfigurations::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

        } else {

            if (!WidgetConfigurations.alreadyAuthenticatedWidgets) {

                if (widgetConfigurationsDependencyInjection.functionsClassLegacy.securityServicesSubscribed()) {

                    SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                        override fun authenticatedFloatIt(extraInformation: Bundle?) {
                            super.authenticatedFloatIt(extraInformation)
                            Log.d(this@WidgetConfigurations.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                            WidgetConfigurations.alreadyAuthenticatedWidgets = true
                        }

                        override fun failedAuthenticated() {
                            super.failedAuthenticated()
                            Log.d(this@WidgetConfigurations.javaClass.simpleName, "FailedAuthenticated")

                            if (AuthenticationFingerprint.attemptCounter == 5) {
                                this@WidgetConfigurations.finish()
                            }

                            WidgetConfigurations.alreadyAuthenticatedWidgets = false
                        }

                        override fun invokedPinPassword() {
                            super.invokedPinPassword()
                            Log.d(this@WidgetConfigurations.javaClass.simpleName, "InvokedPinPassword")
                        }
                    }

                    startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
                        putExtra("OtherTitle", getString(R.string.floatingWidget))
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if (PublicVariable.actionCenter) {
            widgetConfigurationsDependencyInjection.functionsClassLegacy.closeActionMenuOption(this@WidgetConfigurations, widgetConfigurationsViewsBinding.fullActionViews,
                    widgetConfigurationsViewsBinding.actionButton)
        }
    }

    override fun onBackPressed() {
        if (widgetConfigurationsViewsBinding.installedNestedScrollView.isShown) {
            widgetConfigurationsDependencyInjection.functionsClassLegacy.doVibrate(77)

            widgetConfigurationsViewsBinding.installedNestedScrollView.visibility = View.INVISIBLE
            widgetConfigurationsViewsBinding.fastScrollerIndexIncludeInstalled.nestedIndexScrollView.visibility = View.INVISIBLE

            if (!configuredWidgetAvailable) {
                widgetConfigurationsViewsBinding.addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener)
            }

            ViewCompat.animate(widgetConfigurationsViewsBinding.addWidget)
                    .rotation(0.0f)
                    .withLayer()
                    .setDuration(300L)
                    .setInterpolator(OvershootInterpolator(3.0f))
                    .start()
            val startRadius = 0
            val endRadius = hypot(widgetConfigurationsDependencyInjection.functionsClassLegacy.displayX().toDouble(), widgetConfigurationsDependencyInjection.functionsClassLegacy.displayY().toDouble()).toInt()
            val circularReveal = ViewAnimationUtils.createCircularReveal(widgetConfigurationsViewsBinding.installedNestedScrollView,
                    (widgetConfigurationsViewsBinding.addWidget.x + widgetConfigurationsViewsBinding.addWidget.width / 2).roundToInt(),
                    (widgetConfigurationsViewsBinding.addWidget.y + widgetConfigurationsViewsBinding.addWidget.height / 2).roundToInt(),
                    endRadius.toFloat(), startRadius.toFloat())
            circularReveal.duration = 864
            circularReveal.start()
            circularReveal.addListener(object : Animator.AnimatorListener {

                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    widgetConfigurationsViewsBinding.installedNestedScrollView.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })

            if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                if (PublicVariable.themeLightDark) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    }
                }

                val valueAnimator = ValueAnimator
                        .ofArgb(window.navigationBarColor, widgetConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(widgetConfigurationsDependencyInjection.functionsClassLegacy.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f))
                valueAnimator.addUpdateListener { animator ->
                    window.statusBarColor = (animator.animatedValue) as Int
                    window.navigationBarColor = (animator.animatedValue) as Int
                }
                valueAnimator.start()
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                if (PublicVariable.themeLightDark) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    }
                }
                val colorAnimation = ValueAnimator
                        .ofArgb(window.navigationBarColor, PublicVariable.colorLightDark)
                colorAnimation.addUpdateListener { animator ->
                    window.navigationBarColor = (animator.animatedValue) as Int
                    window.statusBarColor = (animator.animatedValue) as Int
                }
                colorAnimation.start()
            }
        } else {

            widgetConfigurationsDependencyInjection.functionsClassLegacy.overrideBackPressToMain(this@WidgetConfigurations, this@WidgetConfigurations)
        }
    }

    override fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {
        super.onSwipeGesture(gestureConstants, downMotionEvent, moveMotionEvent, initVelocityX, initVelocityY)

        when (gestureConstants) {
            is GestureConstants.SwipeHorizontal -> {
                when (gestureConstants.horizontalDirection) {
                    GestureListenerConstants.SWIPE_RIGHT -> {

                        widgetConfigurationsDependencyInjection.functionsClassLegacy.navigateToClass(this@WidgetConfigurations, FoldersConfigurations::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
                    }
                    GestureListenerConstants.SWIPE_LEFT -> {

                        widgetConfigurationsDependencyInjection.functionsClassLegacy.navigateToClass(this@WidgetConfigurations, ApplicationsViewPhone::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
                    }
                }
            }
            else -> {}
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        motionEvent?.let {
            swipeGestureListener.onTouchEvent(it)
        }

        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                InstalledWidgetsAdapter.WIDGET_CONFIGURATION_REQUEST -> {

                    if (InstalledWidgetsAdapter.pickedWidgetPackageName != null
                            && InstalledWidgetsAdapter.pickedWidgetClassNameProvider != null) {

                        CoroutineScope(Dispatchers.IO).launch {

                            val dataExtras = data!!.extras
                            val appWidgetId = dataExtras!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)

                            val widgetDataModel = WidgetDataModel(
                                    System.currentTimeMillis(),
                                    appWidgetId,
                                    InstalledWidgetsAdapter.pickedWidgetPackageName!!,
                                    InstalledWidgetsAdapter.pickedWidgetClassNameProvider!!,
                                    InstalledWidgetsAdapter.pickedWidgetConfigClassName,
                                    widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(InstalledWidgetsAdapter.pickedWidgetPackageName),
                                    InstalledWidgetsAdapter.pickedWidgetLabel,
                                    false
                            )

                            val widgetDataInterface = Room.databaseBuilder(applicationContext, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                    .fallbackToDestructiveMigration()
                                    .addCallback(object : RoomDatabase.Callback() {

                                        override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                            super.onCreate(supportSQLiteDatabase)
                                        }

                                        override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                            super.onOpen(supportSQLiteDatabase)

                                            loadConfiguredWidgets()
                                        }
                                    })
                                    .build()

                            widgetDataInterface.initDataAccessObject().insertNewWidgetDataSuspend(widgetDataModel)
                            widgetDataInterface.close()
                        }
                    }
                }
                InstalledWidgetsAdapter.SYSTEM_WIDGET_PICKER -> {
                    val extras = data!!.extras
                    val appWidgetId = extras!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                    val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)

                    if (appWidgetInfo.configure != null
                            && packageManager.getActivityInfo(appWidgetInfo.configure, PackageManager.GET_META_DATA).exported) {

                        Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                            this.component = appWidgetInfo.configure
                            this.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                            startActivityForResult(this, InstalledWidgetsAdapter.SYSTEM_WIDGET_PICKER_CONFIGURATION)
                        }

                    } else {

                        if (InstalledWidgetsAdapter.pickedWidgetClassNameProvider != null) {

                            CoroutineScope(Dispatchers.IO).launch {

                                val widgetDataModel = WidgetDataModel(
                                        System.currentTimeMillis(),
                                        appWidgetId,
                                        appWidgetInfo.provider.packageName,
                                        InstalledWidgetsAdapter.pickedWidgetClassNameProvider!!,
                                        InstalledWidgetsAdapter.pickedWidgetConfigClassName,
                                        widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(appWidgetInfo.provider.packageName),
                                        appWidgetInfo.loadLabel(packageManager),
                                        false
                                )

                                val widgetDataInterface = Room.databaseBuilder(applicationContext, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                        .fallbackToDestructiveMigration()
                                        .addCallback(object : RoomDatabase.Callback() {

                                            override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                                super.onCreate(supportSQLiteDatabase)
                                            }

                                            override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                                super.onOpen(supportSQLiteDatabase)

                                                loadConfiguredWidgets()
                                            }
                                        })
                                        .build()

                                widgetDataInterface.initDataAccessObject().insertNewWidgetDataSuspend(widgetDataModel)
                                widgetDataInterface.close()
                            }
                        }
                    }
                }
                InstalledWidgetsAdapter.SYSTEM_WIDGET_PICKER_CONFIGURATION -> {

                    if (InstalledWidgetsAdapter.pickedWidgetClassNameProvider != null) {

                        CoroutineScope(Dispatchers.IO).launch {

                            val extras = data!!.extras
                            val appWidgetId = extras!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                            val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)

                            val widgetDataModel = WidgetDataModel(
                                    System.currentTimeMillis(),
                                    appWidgetId,
                                    appWidgetInfo.provider.packageName,
                                    InstalledWidgetsAdapter.pickedWidgetClassNameProvider!!,
                                    InstalledWidgetsAdapter.pickedWidgetConfigClassName,
                                    widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(appWidgetInfo.provider.packageName),
                                    appWidgetInfo.loadLabel(packageManager),
                                    false
                            )
                            val widgetDataInterface = Room.databaseBuilder(applicationContext, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                    .fallbackToDestructiveMigration()
                                    .addCallback(object : RoomDatabase.Callback() {

                                        override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                            super.onCreate(supportSQLiteDatabase)
                                        }

                                        override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                            super.onOpen(supportSQLiteDatabase)

                                            loadConfiguredWidgets()
                                        }
                                    })
                                    .build()
                            widgetDataInterface.initDataAccessObject().insertNewWidgetDataSuspend(widgetDataModel)
                            widgetDataInterface.close()
                        }
                    }
                }
            }
        }
    }

    fun forceLoadConfiguredWidgets() {
        configuredWidgetsAdapterItems.clear()
        configuredWidgetsSections.clear()

        loadConfiguredWidgets()
    }

    fun loadConfiguredWidgets() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

        configuredWidgetsAdapterItems.clear()
        configuredWidgetsSections.clear()
        widgetConfigurationsViewsBinding.configuredWidgetList.removeAllViews()

        if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) {
            widgetConfigurationsViewsBinding.loadingSplash.setBackgroundColor(Color.TRANSPARENT)
        } else {
            widgetConfigurationsViewsBinding.loadingSplash.setBackgroundColor(window.navigationBarColor)
        }

        val typeface = Typeface.createFromAsset(assets, "upcil.ttf")
        widgetConfigurationsViewsBinding.loadingText.typeface = typeface

        if (PublicVariable.themeLightDark) {

            widgetConfigurationsViewsBinding.loadingProgress
                    .indeterminateDrawable.setTint(PublicVariable.darkMutedColor)
            widgetConfigurationsViewsBinding.loadingText.setTextColor(getColor(R.color.dark))

        } else if (!PublicVariable.themeLightDark) {

            widgetConfigurationsViewsBinding.loadingProgress
                    .indeterminateDrawable.setTint(PublicVariable.vibrantColor)
            widgetConfigurationsViewsBinding.loadingText.setTextColor(getColor(R.color.light))

        }

        configuredWidgetsAdapterItems.clear()
        configuredWidgetsSections.clear()

        configuredWidgetAvailable = false

        if (widgetConfigurationsDependencyInjection.functionsClassLegacy.customIconsEnable()) {
            loadCustomIcons.load()
            Debug.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.totalIconsNumber)
        }

        val widgetDataInterface = Room.databaseBuilder(applicationContext, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {

                    override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                        super.onCreate(supportSQLiteDatabase)
                    }

                    override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                        super.onOpen(supportSQLiteDatabase)
                    }
                })
                .build()

        val widgetDataModels: List<WidgetDataModel> = widgetDataInterface.initDataAccessObject().getAllWidgetDataSuspend()

        if (widgetDataModels.isNotEmpty()) {
            var oldAppName = ""
            var widgetIndex = 0

            widgetDataModels.asFlow()
                    .onCompletion {

                        /*Search Engine*/
                        SearchEngine(activity = this@WidgetConfigurations, context = applicationContext,
                                searchEngineViewBinding = widgetConfigurationsViewsBinding.searchEngineViewInclude,
                                functionsClassLegacy = widgetConfigurationsDependencyInjection.functionsClassLegacy,
                                fileIO = widgetConfigurationsDependencyInjection.fileIO,
                                floatingServices = widgetConfigurationsDependencyInjection.floatingServices,
                                customIcons = loadCustomIcons,
                                firebaseAuth = firebaseAuth).apply {

                            this.initializeSearchEngineData()
                        }
                        /*Search Engine*/

                        loadWidgetsIndexConfigured().await()

                        loadInstalledCustomIconPackages().await()
                    }
                    .withIndex().collect { widgetDataModel ->
                        val appWidgetId: Int = widgetDataModel.value.WidgetId
                        val packageName: String = widgetDataModel.value.PackageName
                        val className: String = widgetDataModel.value.ClassNameProvider
                        val configClassName: String? = widgetDataModel.value.ConfigClassName

                        Debug.PrintDebug("*** $appWidgetId *** PackageName: $packageName - ClassName: $className - Configure: $configClassName ***")

                        if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appIsInstalled(packageName)) {
                            val appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
                            val newAppName = widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(packageName)
                            val appIcon = if (widgetConfigurationsDependencyInjection.functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(packageName, widgetConfigurationsDependencyInjection.functionsClassLegacy.shapedAppIcon(packageName)) else widgetConfigurationsDependencyInjection.functionsClassLegacy.shapedAppIcon(packageName)
                            if (widgetIndex == 0) {
                                configuredWidgetsSections.add(WidgetSectionedConfiguredAdapter.Section(widgetIndex, newAppName, appIcon))
                                indexListConfigured.add(newAppName.substring(0, 1).toUpperCase())
                            } else {
                                if (oldAppName != newAppName) {
                                    configuredWidgetsSections.add(WidgetSectionedConfiguredAdapter.Section(widgetIndex, newAppName, appIcon))
                                    indexListConfigured.add(newAppName.substring(0, 1).toUpperCase())
                                }
                            }
                            oldAppName = widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(packageName)
                            indexListConfigured.add(newAppName.substring(0, 1).toUpperCase())
                            configuredWidgetsAdapterItems.add(AdapterItems(
                                    newAppName,
                                    packageName,
                                    className,
                                    configClassName,
                                    widgetDataModel.value.WidgetLabel,
                                    appIcon,
                                    appWidgetProviderInfo,
                                    appWidgetId,
                                    widgetDataModel.value.Recovery ?: false,
                                    SearchResultType.SearchWidgets
                            ))

                            widgetIndex++
                        } else {
                            widgetDataInterface.initDataAccessObject().deleteByWidgetClassNameProviderWidgetSuspend(packageName, className)
                        }
                    }

            configuredWidgetsRecyclerViewAdapter = ConfiguredWidgetsAdapter(this@WidgetConfigurations,
                    configuredWidgetsAdapterItems,
                    appWidgetManager, appWidgetHost)
        }

        configuredWidgetAvailable = configuredWidgetsAdapterItems.isNotEmpty()

        widgetDataInterface.close()

        if (configuredWidgetAvailable) {

            widgetConfigurationsViewsBinding.reconfigure.visibility = View.VISIBLE

            val sectionsData = arrayOfNulls<WidgetSectionedConfiguredAdapter.Section>(configuredWidgetsSections.size)
            widgetSectionedConfiguredAdapter = WidgetSectionedConfiguredAdapter(
                    applicationContext,
                    R.layout.widgets_sections,
                    widgetConfigurationsViewsBinding.configuredWidgetList,
                    configuredWidgetsRecyclerViewAdapter
            )
            widgetSectionedConfiguredAdapter.setSections(configuredWidgetsSections.toArray(sectionsData))

            widgetConfigurationsViewsBinding.configuredWidgetList.adapter = widgetSectionedConfiguredAdapter

            configuredWidgetsRecyclerViewAdapter.notifyDataSetChanged()
            widgetSectionedConfiguredAdapter.notifyDataSetChanged()

            delay(333)

            widgetConfigurationsViewsBinding.configuredWidgetNestedScrollView.scrollTo(0, 0)

            val animation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            widgetConfigurationsViewsBinding.loadingSplash.visibility = View.INVISIBLE
            widgetConfigurationsViewsBinding.loadingSplash.startAnimation(animation)
        } else {

            widgetConfigurationsViewsBinding.reconfigure.visibility = View.INVISIBLE

            installedWidgetsLoaded = false
            widgetConfigurationsViewsBinding.addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener)
            widgetConfigurationsViewsBinding.loadingSplash.visibility = View.VISIBLE
        }
    }

    fun loadInstalledWidgets() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

        widgetConfigurationsViewsBinding.loadingInstalledWidgets.setColor(PublicVariable.primaryColor)
        widgetConfigurationsViewsBinding.loadingInstalledWidgets.visibility = View.VISIBLE

        widgetConfigurationsViewsBinding.installedNestedScrollView.setBackgroundColor(if (PublicVariable.themeLightDark) getColor(R.color.transparent_light_high_twice) else getColor(R.color.transparent_dark_high_twice))

        installedWidgetsAdapterItems.clear()
        installedWidgetsSections.clear()

        widgetProviderInfoList = appWidgetManager.installedProviders as ArrayList<AppWidgetProviderInfo>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            widgetProviderInfoList.sortWith(Comparator { appWidgetProviderInfoLeft, appWidgetProviderInfoRight ->

                widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(appWidgetProviderInfoLeft.provider.packageName)
                        .compareTo(widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(appWidgetProviderInfoRight.provider.packageName))
            })
        }

        if (widgetConfigurationsDependencyInjection.functionsClassLegacy.customIconsEnable()) {
            loadCustomIcons.load()
            Debug.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.totalIconsNumber)
        }

        var oldAppName = ""
        var widgetIndex = 0

        widgetProviderInfoList.asFlow()
                .onEach {
                    Debug.PrintDebug("*** Provider = " + it.provider + " | Config = " + it.configure + " ***")
                }
                .onCompletion {

                    loadWidgetsIndexInstalled()
                }
                .withIndex().collect { appWidgetProviderInfo ->
                    val packageName: String = appWidgetProviderInfo.value.provider.packageName
                    val className: String = appWidgetProviderInfo.value.provider.className
                    val componentNameConfiguration: ComponentName? = appWidgetProviderInfo.value.configure

                    if (componentNameConfiguration != null) {//Configurable Widget

                        try {
                            if (packageManager.getActivityInfo(componentNameConfiguration, PackageManager.GET_META_DATA).exported) {

                                if (packageName.isNotEmpty() && className.isNotEmpty()) {

                                    val newAppName = widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(packageName)
                                    val newAppIcon = if (widgetConfigurationsDependencyInjection.functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(packageName, widgetConfigurationsDependencyInjection.functionsClassLegacy.shapedAppIcon(packageName)) else widgetConfigurationsDependencyInjection.functionsClassLegacy.shapedAppIcon(packageName)

                                    if (widgetIndex == 0) {
                                        installedWidgetsSections.add(WidgetSectionedInstalledAdapter.Section(widgetIndex, newAppName, newAppIcon))
                                        indexListInstalled.add(newAppName.substring(0, 1).toUpperCase(Locale.getDefault()))
                                    } else {
                                        if (oldAppName != newAppName) {
                                            installedWidgetsSections.add(WidgetSectionedInstalledAdapter.Section(widgetIndex, newAppName, newAppIcon))
                                            indexListInstalled.add(newAppName.substring(0, 1).toUpperCase(Locale.getDefault()))
                                        }
                                    }

                                    oldAppName = widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(appWidgetProviderInfo.value.provider.packageName)

                                    val widgetPreviewDrawable: Drawable? = appWidgetProviderInfo.value.loadPreviewImage(applicationContext, DisplayMetrics.DENSITY_HIGH)
                                    val widgetLabel: String? = appWidgetProviderInfo.value.loadLabel(packageManager)

                                    indexListInstalled.add(newAppName.substring(0, 1).toUpperCase(Locale.getDefault()))
                                    installedWidgetsAdapterItems.add(AdapterItems(widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(appWidgetProviderInfo.value.provider.packageName),
                                            appWidgetProviderInfo.value.provider.packageName,
                                            appWidgetProviderInfo.value.provider.className,
                                            componentNameConfiguration.className,
                                            widgetLabel ?: newAppName,
                                            newAppIcon,
                                            widgetPreviewDrawable
                                                    ?: appWidgetProviderInfo.value.loadIcon(applicationContext, DisplayMetrics.DENSITY_HIGH),
                                            appWidgetProviderInfo.value
                                    ))

                                }
                            }
                        } catch (e: PackageManager.NameNotFoundException) {
                            e.printStackTrace()

                            //Other Idiot Developers Could Not Even Create A Simple Widget - Idiot Developers Forgot To Setup Configuration Activity Or Remove It.
                            val newAppName = widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(packageName)
                            val newAppIcon = if (widgetConfigurationsDependencyInjection.functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(packageName, widgetConfigurationsDependencyInjection.functionsClassLegacy.shapedAppIcon(packageName)) else widgetConfigurationsDependencyInjection.functionsClassLegacy.shapedAppIcon(packageName)

                            if (widgetIndex == 0) {
                                installedWidgetsSections.add(WidgetSectionedInstalledAdapter.Section(widgetIndex, newAppName, newAppIcon))
                                indexListInstalled.add(newAppName.substring(0, 1).toUpperCase(Locale.getDefault()))
                            } else {
                                if (oldAppName != newAppName) {
                                    installedWidgetsSections.add(WidgetSectionedInstalledAdapter.Section(widgetIndex, newAppName, newAppIcon))
                                    indexListInstalled.add(newAppName.substring(0, 1).toUpperCase(Locale.getDefault()))
                                }
                            }

                            oldAppName = widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(appWidgetProviderInfo.value.provider.packageName)
                            val widgetPreviewDrawable: Drawable? = appWidgetProviderInfo.value.loadPreviewImage(applicationContext, DisplayMetrics.DENSITY_HIGH)
                            val widgetLabel: String? = appWidgetProviderInfo.value.loadLabel(packageManager)

                            indexListInstalled.add(newAppName.substring(0, 1).toUpperCase(Locale.getDefault()))
                            installedWidgetsAdapterItems.add(AdapterItems(widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(appWidgetProviderInfo.value.provider.packageName),
                                    appWidgetProviderInfo.value.provider.packageName,
                                    appWidgetProviderInfo.value.provider.className,
                                    null,
                                    if (widgetLabel != null) {
                                        "$widgetLabel ⚠"
                                    } else {
                                        "$newAppName ⚠"
                                    },
                                    newAppIcon,
                                    widgetPreviewDrawable
                                            ?: appWidgetProviderInfo.value.loadIcon(applicationContext, DisplayMetrics.DENSITY_HIGH),
                                    appWidgetProviderInfo.value
                            ))

                        }

                    } else {//Normal - Not Configurable Widget

                        if (packageName.isNotEmpty() && className.isNotEmpty()) {

                            val newAppName = widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(packageName)
                            val newAppIcon = if (widgetConfigurationsDependencyInjection.functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(packageName, widgetConfigurationsDependencyInjection.functionsClassLegacy.shapedAppIcon(packageName)) else widgetConfigurationsDependencyInjection.functionsClassLegacy.shapedAppIcon(packageName)

                            if (widgetIndex == 0) {
                                installedWidgetsSections.add(WidgetSectionedInstalledAdapter.Section(widgetIndex, newAppName, newAppIcon))
                                indexListInstalled.add(newAppName.substring(0, 1).toUpperCase(Locale.getDefault()))
                            } else {
                                if (oldAppName != newAppName) {
                                    installedWidgetsSections.add(WidgetSectionedInstalledAdapter.Section(widgetIndex, newAppName, newAppIcon))
                                    indexListInstalled.add(newAppName.substring(0, 1).toUpperCase(Locale.getDefault()))
                                }
                            }

                            oldAppName = widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(appWidgetProviderInfo.value.provider.packageName)
                            val widgetPreviewDrawable: Drawable? = appWidgetProviderInfo.value.loadPreviewImage(applicationContext, DisplayMetrics.DENSITY_HIGH)
                            val widgetLabel: String? = appWidgetProviderInfo.value.loadLabel(packageManager)

                            indexListInstalled.add(newAppName.substring(0, 1).toUpperCase(Locale.getDefault()))
                            installedWidgetsAdapterItems.add(AdapterItems(widgetConfigurationsDependencyInjection.functionsClassLegacy.applicationName(appWidgetProviderInfo.value.provider.packageName),
                                    appWidgetProviderInfo.value.provider.packageName,
                                    appWidgetProviderInfo.value.provider.className,
                                    null,
                                    widgetLabel ?: newAppName,
                                    newAppIcon,
                                    widgetPreviewDrawable
                                            ?: appWidgetProviderInfo.value.loadIcon(applicationContext, DisplayMetrics.DENSITY_HIGH),
                                    appWidgetProviderInfo.value
                            ))
                        }

                    }

                    widgetIndex++
                }

        installedWidgetsRecyclerViewAdapter = InstalledWidgetsAdapter(this@WidgetConfigurations, applicationContext, installedWidgetsAdapterItems, appWidgetHost)

        installedWidgetsLoaded = true

        val viewPropertyAnimator: ViewPropertyAnimator = widgetConfigurationsViewsBinding.addWidget.animate()
                .rotation(135.0f)
                .withLayer()
                .setDuration(500L)
                .setInterpolator(OvershootInterpolator(13.0f))
        viewPropertyAnimator.start()

        widgetConfigurationsViewsBinding.installedNestedScrollView.setBackgroundColor(if (PublicVariable.themeLightDark) getColor(R.color.transparent_light) else getColor(R.color.dark_transparent))
        widgetConfigurationsViewsBinding.installedNestedScrollView.visibility = View.VISIBLE

        installedWidgetsRecyclerViewAdapter.notifyDataSetChanged()
        val sectionsData = arrayOfNulls<WidgetSectionedInstalledAdapter.Section>(installedWidgetsSections.size)
        widgetSectionedInstalledAdapter = WidgetSectionedInstalledAdapter(
                applicationContext,
                R.layout.widgets_sections,
                widgetConfigurationsViewsBinding.installedWidgetList,
                installedWidgetsRecyclerViewAdapter
        )
        widgetSectionedInstalledAdapter.setSections(installedWidgetsSections.toArray(sectionsData))
        widgetSectionedInstalledAdapter.notifyDataSetChanged()
        widgetConfigurationsViewsBinding.installedWidgetList.adapter = widgetSectionedInstalledAdapter

        val xPosition = (widgetConfigurationsViewsBinding.addWidget.x + widgetConfigurationsViewsBinding.addWidget.width / 2).roundToInt()
        val yPosition = (widgetConfigurationsViewsBinding.addWidget.y + widgetConfigurationsViewsBinding.addWidget.height / 2).roundToInt()
        val startRadius = 0
        val endRadius = hypot(widgetConfigurationsDependencyInjection.functionsClassLegacy.displayX().toDouble(), widgetConfigurationsDependencyInjection.functionsClassLegacy.displayY().toDouble()).toInt()
        val circularReveal = ViewAnimationUtils.createCircularReveal(widgetConfigurationsViewsBinding.installedNestedScrollView,
                xPosition, yPosition,
                startRadius.toFloat(), endRadius.toFloat())
        circularReveal.duration = 864
        circularReveal.start()
        circularReveal.addListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animator: Animator) {

            }

            override fun onAnimationEnd(animator: Animator) {
                widgetConfigurationsViewsBinding.installedNestedScrollView.visibility = View.VISIBLE

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                if (widgetConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) {
                    val colorAnimation = ValueAnimator
                            .ofArgb(window.navigationBarColor, if (PublicVariable.themeLightDark) getColor(R.color.fifty_light_twice) else getColor(R.color.transparent_dark_high_twice))
                    colorAnimation.addUpdateListener { animator ->
                        window.statusBarColor = (animator.animatedValue) as Int
                        window.navigationBarColor = (animator.animatedValue) as Int
                    }
                    colorAnimation.start()
                } else {
                    if (PublicVariable.themeLightDark) {
                        widgetConfigurationsViewsBinding.installedNestedScrollView.setBackground(ColorDrawable(getColor(R.color.transparent_light)))
                        if (PublicVariable.themeLightDark) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                            }
                        }
                        val colorAnimation = ValueAnimator
                                .ofArgb(window.navigationBarColor, widgetConfigurationsDependencyInjection.functionsClassLegacy.mixColors(getColor(R.color.light), getWindow().navigationBarColor, 0.70f))
                        colorAnimation.addUpdateListener { animator ->
                            window.navigationBarColor = (animator.animatedValue) as Int
                            window.statusBarColor = (animator.animatedValue) as Int
                        }
                        colorAnimation.start()
                    } else if (!PublicVariable.themeLightDark) {
                        widgetConfigurationsViewsBinding.installedNestedScrollView.setBackground(ColorDrawable(getColor(R.color.dark_transparent)))
                        val colorAnimation = ValueAnimator
                                .ofArgb(getWindow().navigationBarColor, widgetConfigurationsDependencyInjection.functionsClassLegacy.mixColors(getColor(R.color.dark), getWindow().navigationBarColor, 0.70f))
                        colorAnimation.addUpdateListener { animator ->
                            window.navigationBarColor = (animator.animatedValue) as Int
                            window.statusBarColor = (animator.animatedValue) as Int
                        }
                        colorAnimation.start()
                    }
                }
            }

            override fun onAnimationCancel(animator: Animator) {

            }

            override fun onAnimationRepeat(animator: Animator) {

            }
        })

        if (!getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists() || !configuredWidgetAvailable) {
            delay(200)

            val animation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            widgetConfigurationsViewsBinding.loadingSplash.visibility = View.INVISIBLE
            widgetConfigurationsViewsBinding.loadingSplash.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation) {
                    (findViewById<View>(R.id.switchFloating) as LinearLayout).visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation) {

                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }

        widgetConfigurationsViewsBinding.loadingInstalledWidgets.visibility = View.INVISIBLE
    }

    private fun loadWidgetsIndexConfigured() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
        /*Indexed Popup Fast Scroller*/
        val indexedFastScroller: IndexedFastScroller = IndexedFastScroller(
                context = applicationContext,
                layoutInflater = layoutInflater,
                rootView = widgetConfigurationsViewsBinding.MainView,
                nestedScrollView = widgetConfigurationsViewsBinding.configuredWidgetNestedScrollView,
                recyclerView = widgetConfigurationsViewsBinding.configuredWidgetList,
                fastScrollerIndexViewBinding = widgetConfigurationsViewsBinding.fastScrollerIndexIncludeConfigured,
                indexedFastScrollerFactory = IndexedFastScrollerFactory(
                        popupEnable = !widgetConfigurationsDependencyInjection.functionsClassLegacy.litePreferencesEnabled(),
                        popupTextColor = PublicVariable.colorLightDarkOpposite,
                        indexItemTextColor = PublicVariable.colorLightDarkOpposite)
        )
        indexedFastScroller.initializeIndexView().await()
                .loadIndexData(listOfNewCharOfItemsForIndex = indexListConfigured).await()
        /*Indexed Popup Fast Scroller*/
    }

    private fun loadWidgetsIndexInstalled() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
        /*Indexed Popup Fast Scroller*/
        val indexedFastScroller: IndexedFastScroller = IndexedFastScroller(
                context = applicationContext,
                layoutInflater = layoutInflater,
                rootView = widgetConfigurationsViewsBinding.MainView,
                nestedScrollView = widgetConfigurationsViewsBinding.installedNestedScrollView,
                recyclerView = widgetConfigurationsViewsBinding.installedWidgetList,
                fastScrollerIndexViewBinding = widgetConfigurationsViewsBinding.fastScrollerIndexIncludeInstalled,
                indexedFastScrollerFactory = IndexedFastScrollerFactory(
                        popupEnable = !widgetConfigurationsDependencyInjection.functionsClassLegacy.litePreferencesEnabled(),
                        popupTextColor = PublicVariable.colorLightDarkOpposite,
                        indexItemTextColor = PublicVariable.colorLightDarkOpposite)
        )
        indexedFastScroller.initializeIndexView().await()
                .loadIndexData(listOfNewCharOfItemsForIndex = indexListInstalled).await()
        /*Indexed Popup Fast Scroller*/
    }

    private fun loadInstalledCustomIconPackages() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
        val packageManager = applicationContext.packageManager
        //ACTION: com.novalauncher.THEME
        //CATEGORY: com.novalauncher.category.CUSTOM_ICON_PICKER
        val intentCustomIcons = Intent()
        intentCustomIcons.action = "com.novalauncher.THEME"
        intentCustomIcons.addCategory("com.novalauncher.category.CUSTOM_ICON_PICKER")
        val resolveInfos = packageManager.queryIntentActivities(intentCustomIcons, 0)

        PublicVariable.customIconsPackages.clear()
        for (resolveInfo in resolveInfos) {
            Debug.PrintDebug("CustomIconPackages ::: " + resolveInfo.activityInfo.packageName)
            PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName)
        }
    }

    fun createWidget(context: Context, widgetView: ViewGroup, appWidgetManager: AppWidgetManager, appWidgetHost: AppWidgetHost, appWidgetProviderInfo: AppWidgetProviderInfo, widgetId: Int) {
        widgetView.removeAllViews()

        val functionsClass = FunctionsClassLegacy(context)

        appWidgetHost.startListening()

        val hostView = appWidgetHost.createView(context, widgetId, appWidgetProviderInfo)
        hostView.setAppWidget(widgetId, appWidgetProviderInfo)

        val widgetWidth = 213
        val widgetHeight = 213

        hostView.minimumWidth = widgetWidth
        hostView.minimumHeight = widgetHeight

        val bundle = Bundle()
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, widgetWidth)
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, widgetHeight)
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, functionsClass.displayX() / 2)
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, functionsClass.displayY() / 2)
        appWidgetManager.bindAppWidgetIdIfAllowed(widgetId, appWidgetProviderInfo.provider, bundle)

        widgetView.addView(hostView)
    }

    val scaleDownListener: Animator.AnimatorListener = object : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator) {

        }

        override fun onAnimationRepeat(animation: Animator) {

        }

        override fun onAnimationEnd(animation: Animator) {
            if (!installedWidgetsLoaded) {
                widgetConfigurationsViewsBinding.addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener)
            }
        }

        override fun onAnimationCancel(animation: Animator) {

        }
    }

    val scaleUpListener: Animator.AnimatorListener = object : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator) {

        }

        override fun onAnimationRepeat(animation: Animator) {

        }

        override fun onAnimationEnd(animation: Animator) {
            widgetConfigurationsViewsBinding.addWidget.animate().scaleXBy(-0.23f).scaleYBy(-0.23f).setDuration(323).setListener(scaleDownListener)
        }

        override fun onAnimationCancel(animation: Animator) {

        }
    }
}