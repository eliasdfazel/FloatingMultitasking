/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 1:15 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geeksempire.primepuzzles.GameView.UI.SwipeGestureListener
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.SearchEngineAdapter
import net.geekstools.floatshort.PRO.Shortcuts.ApplicationsView
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsSearchEngine
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassRunServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassSecurity
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.GeneralAdapters.RecycleViewSmoothLayoutGrid
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.floatshort.PRO.Widget.WidgetsAdapter.WidgetSectionedGridRecyclerViewAdapter
import net.geekstools.floatshort.PRO.databinding.WidgetConfigurationsViewsBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.math.hypot
import kotlin.math.roundToInt

class WidgetConfigurationsXYZ : AppCompatActivity(), GestureListenerInterface {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext, this@WidgetConfigurationsXYZ)
    }
    private val functionsClassSecurity: FunctionsClassSecurity by lazy {
        FunctionsClassSecurity(this@WidgetConfigurationsXYZ, applicationContext)
    }
    private val functionsClassRunServices: FunctionsClassRunServices by lazy {
        FunctionsClassRunServices(applicationContext)
    }

    /*Search Engine*/
    private lateinit var searchRecyclerViewAdapter: SearchEngineAdapter
    lateinit var searchAdapterItems: ArrayList<AdapterItemsSearchEngine>
    /*Search Engine*/

    private val mapIndexFirstItem: Map<String, Int> = LinkedHashMap<String, Int>()
    private val mapIndexLastItem: Map<String, Int> = LinkedHashMap<String, Int>()
    private val mapIndexFirstItemInstalled: Map<String, Int> = LinkedHashMap<String, Int>()
    private val mapIndexLastItemInstalled: Map<String, Int> = LinkedHashMap<String, Int>()

    private val mapRangeIndex: Map<Int, String> = LinkedHashMap<Int, String>()
    private val mapRangeIndexInstalled: Map<Int, String> = LinkedHashMap<Int, String>()

    private val indexItems: NavigableMap<String, Int> = TreeMap<String, Int>()
    private val indexItemsInstalled: NavigableMap<String, Int> = TreeMap<String, Int>()

    private val indexListConfigured: List<String> = ArrayList<String>()
    private val indexListInstalled: List<String> = ArrayList<String>()

    private val installedWidgetsSections: List<WidgetSectionedGridRecyclerViewAdapter.Section> = ArrayList<WidgetSectionedGridRecyclerViewAdapter.Section>()
    private val configuredWidgetsSections: List<WidgetSectionedGridRecyclerViewAdapter.Section> = ArrayList<WidgetSectionedGridRecyclerViewAdapter.Section>()

    private lateinit var installedWidgetsRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var configuredWidgetsRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var configuredWidgetsSectionedGridRecyclerViewAdapter: WidgetSectionedGridRecyclerViewAdapter
    private lateinit var installedWidgetsRecyclerViewLayoutManager: GridLayoutManager
    private lateinit var configuredWidgetsRecyclerViewLayoutManager: GridLayoutManager

    private lateinit var widgetProviderInfoList: List<AppWidgetProviderInfo>
    private val installedWidgetsAdapterItems: ArrayList<AdapterItems> = ArrayList<AdapterItems>()
    private val configuredWidgetsAdapterItems: ArrayList<AdapterItems> = ArrayList<AdapterItems>()

    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var appWidgetHost: AppWidgetHost

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(applicationContext, this@WidgetConfigurationsXYZ)
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

        functionsClass.loadSavedColor()
        functionsClass.checkLightDarkTheme()

        if (!functionsClass.readPreference("WidgetsInformation", "Reallocated", true)
                && getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
            startActivity(Intent(applicationContext, WidgetsReallocationProcess::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
            finish()
            return
        }

        widgetConfigurationsViewsBinding.widgetPickerTitle.text = Html.fromHtml(getString(net.geekstools.floatshort.PRO.R.string.widgetPickerTitle))
        widgetConfigurationsViewsBinding.widgetPickerTitle.setTextColor(if (PublicVariable.themeLightDark) getColor(R.color.dark) else getColor(net.geekstools.floatshort.PRO.R.color.light))

        installedWidgetsRecyclerViewLayoutManager = RecycleViewSmoothLayoutGrid(applicationContext, functionsClass.columnCount(190), OrientationHelper.VERTICAL, false)
        widgetConfigurationsViewsBinding.installedWidgetList.layoutManager = installedWidgetsRecyclerViewLayoutManager

        if (functionsClass.appThemeTransparent()) {
            functionsClass.setThemeColorFloating(widgetConfigurationsViewsBinding.wholeWidget, true)
        } else {
            functionsClass.setThemeColorFloating(widgetConfigurationsViewsBinding.wholeWidget, false)
        }

        appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        appWidgetHost = AppWidgetHost(applicationContext, System.currentTimeMillis().toInt())

        if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {

            LoadConfiguredWidgets().invokeOnCompletion {

            }
        } else {
            widgetConfigurationsViewsBinding.reconfigure.visibility = View.INVISIBLE

            widgetConfigurationsViewsBinding.actionButton.bringToFront()
            widgetConfigurationsViewsBinding.addWidget.bringToFront()

            widgetConfigurationsViewsBinding.addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener)

            if (functionsClass.appThemeTransparent()) {
                widgetConfigurationsViewsBinding.loadingSplash.setBackgroundColor(Color.TRANSPARENT)
            } else {
                widgetConfigurationsViewsBinding.loadingSplash.setBackgroundColor(window.navigationBarColor)
            }

            val typeface = Typeface.createFromAsset(assets, "upcil.ttf")
            widgetConfigurationsViewsBinding.loadingText.setTypeface(typeface)

            if (PublicVariable.themeLightDark) {
                widgetConfigurationsViewsBinding.loadingProgress.getIndeterminateDrawable().setColorFilter(PublicVariable.darkMutedColor, PorterDuff.Mode.MULTIPLY)
                widgetConfigurationsViewsBinding.loadingText.setTextColor(getColor(R.color.dark))
            } else if (!PublicVariable.themeLightDark) {
                widgetConfigurationsViewsBinding.loadingProgress.getIndeterminateDrawable().setColorFilter(PublicVariable.vibrantColor, PorterDuff.Mode.MULTIPLY)
                widgetConfigurationsViewsBinding.loadingText.setTextColor(getColor(R.color.light))
            }

            widgetConfigurationsViewsBinding.switchFloating.bringToFront()

            widgetConfigurationsViewsBinding.textInputSearchView.bringToFront()
            widgetConfigurationsViewsBinding.searchView.bringToFront()
            widgetConfigurationsViewsBinding.searchIcon.bringToFront()
            widgetConfigurationsViewsBinding.searchFloatIt.bringToFront()
            widgetConfigurationsViewsBinding.searchClose.bringToFront()

            LoadSearchEngineData()
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
        if (PublicVariable.themeLightDark /*light*/ && functionsClass.appThemeTransparent() /*transparent*/) {
            widgetConfigurationsViewsBinding.switchApps.setTextColor(getColor(R.color.dark))
            widgetConfigurationsViewsBinding.switchCategories.setTextColor(getColor(R.color.dark))
        }

        widgetConfigurationsViewsBinding.switchCategories.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        widgetConfigurationsViewsBinding.switchCategories.setRippleColor(ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite))

        widgetConfigurationsViewsBinding.switchApps.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        widgetConfigurationsViewsBinding.switchApps.rippleColor = ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        widgetConfigurationsViewsBinding.recoveryAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        widgetConfigurationsViewsBinding.recoveryAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        widgetConfigurationsViewsBinding.automationAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        widgetConfigurationsViewsBinding.automationAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        val drawRecoverFloatingCategories = getDrawable(R.drawable.draw_recovery)?.mutate() as LayerDrawable
        val backgroundRecoverFloatingCategories = drawRecoverFloatingCategories.findDrawableByLayerId(R.id.backgroundTemporary).mutate()
        backgroundRecoverFloatingCategories.setTint(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        widgetConfigurationsViewsBinding.recoverFloatingCategories.setImageDrawable(drawRecoverFloatingCategories)
        widgetConfigurationsViewsBinding.recoverFloatingApps.setImageDrawable(drawRecoverFloatingCategories)

        widgetConfigurationsViewsBinding.actionButton.setOnClickListener {
            functionsClass.doVibrate(33)

            if (!PublicVariable.actionCenter) {
                if (widgetConfigurationsViewsBinding.installedNestedScrollView.isShown) {
                    widgetConfigurationsViewsBinding.installedNestedIndexScrollView.visibility = View.INVISIBLE

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
                    val endRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()

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

                    if (functionsClass.appThemeTransparent()) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                        if (PublicVariable.themeLightDark) {
                            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                            }
                        }
                        val valueAnimator = ValueAnimator
                                .ofArgb(window.navigationBarColor, functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f))
                        valueAnimator.addUpdateListener { animator ->
                            window.statusBarColor = (animator.animatedValue as Int)
                            window.navigationBarColor = (animator.animatedValue as Int)
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
                            window.navigationBarColor = (animator.animatedValue as Int)
                            window.statusBarColor = (animator.animatedValue as Int)
                        }
                        colorAnimation.start()
                    }
                }

                val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(widgetConfigurationsViewsBinding.recoveryAction,
                        widgetConfigurationsViewsBinding.actionButton.x.roundToInt(),
                        widgetConfigurationsViewsBinding.actionButton.y.roundToInt(),
                        finalRadius.toFloat(), functionsClass.DpToInteger(13).toFloat())
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

                functionsClass.openActionMenuOption(widgetConfigurationsViewsBinding.fullActionViews,
                        widgetConfigurationsViewsBinding.actionButton,
                        widgetConfigurationsViewsBinding.fullActionViews.isShown)

            } else {
                widgetConfigurationsViewsBinding.recoveryAction.visibility = View.VISIBLE

                val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(widgetConfigurationsViewsBinding.recoveryAction,
                        widgetConfigurationsViewsBinding.actionButton.x.roundToInt(),
                        widgetConfigurationsViewsBinding.actionButton.y.roundToInt(),
                        functionsClass.DpToInteger(13).toFloat(), finalRadius.toFloat())
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

                functionsClass.closeActionMenuOption(widgetConfigurationsViewsBinding.fullActionViews,
                        widgetConfigurationsViewsBinding.actionButton)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {
        super.onSwipeGesture(gestureConstants, downMotionEvent, moveMotionEvent, initVelocityX, initVelocityY)

        when (gestureConstants) {
            is GestureConstants.SwipeHorizontal -> {
                when (gestureConstants.horizontalDirection) {
                    GestureListenerConstants.SWIPE_RIGHT -> {

                        functionsClass.navigateToClass(FoldersConfigurations::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
                    }
                    GestureListenerConstants.SWIPE_LEFT -> {

                        functionsClass.navigateToClass(ApplicationsView::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        swipeGestureListener.onTouchEvent(motionEvent)

        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun LoadConfiguredWidgets() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

    }

    fun LoadInstalledWidgets() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

    }

    fun LoadApplicationsIndexConfigured() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

    }

    fun LoadApplicationsIndexInstalled() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

    }

    fun createWidget(context: Context?, widgetView: ViewGroup, appWidgetManager: AppWidgetManager, appWidgetHost: AppWidgetHost, appWidgetProviderInfo: AppWidgetProviderInfo, widgetId: Int) {

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

    fun setupFastScrollingIndexingConfigured() {

    }

    fun setupFastScrollingIndexingInstalled() {

    }

    /*Search Engine*/
    fun LoadSearchEngineData() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    }

    fun setupSearchView() {

    }
    /*Search Engine*/
}