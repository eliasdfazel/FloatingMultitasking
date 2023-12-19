/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widgets.FloatingServices

import android.annotation.SuppressLint
import android.app.Service
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Widgets.Utils.FunctionsClassWidgets
import net.geekstools.floatshort.PRO.databinding.FloatingWidgetsBinding

class WidgetUnlimitedFloating : Service() {

    private lateinit var functionsClassLegacy: FunctionsClassLegacy
    private lateinit var functionsClassWidgets: FunctionsClassWidgets

    private lateinit var windowManager: WindowManager

    private val layoutParams: ArrayList<WindowManager.LayoutParams> = ArrayList<WindowManager.LayoutParams>()

    private val widgetLayout: ArrayList<ViewGroup> = ArrayList<ViewGroup>()

    private val wholeViewWidget: ArrayList<RelativeLayout> = ArrayList<RelativeLayout>()
    private val widgetLabel: ArrayList<TextView> = ArrayList<TextView>()
    private val widgetMoveButton: ArrayList<ImageView> = ArrayList<ImageView>()
    private val widgetCloseButton: ArrayList<ImageView> = ArrayList<ImageView>()
    private val widgetResizeControl: ArrayList<Button> = ArrayList<Button>()

    lateinit var appWidgetManager: AppWidgetManager
    private val appWidgetProviderInfo: ArrayList<AppWidgetProviderInfo> = ArrayList<AppWidgetProviderInfo>()
    private val appWidgetHosts: ArrayList<AppWidgetHost> = ArrayList<AppWidgetHost>()
    private val appWidgetHostView: ArrayList<AppWidgetHostView> = ArrayList<AppWidgetHostView>()

    private val appWidgetId: ArrayList<Int> = ArrayList<Int>()
    private val widgetColor: ArrayList<Int> = ArrayList<Int>()

    private val startIdCounter: ArrayList<Int> = ArrayList<Int>()

    private val layoutInflater: LayoutInflater by lazy {
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    private val floatingWidgetsBinding: ArrayList<FloatingWidgetsBinding> = ArrayList<FloatingWidgetsBinding>()

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {

                for (J in 0 until floatingWidgetsBinding.size) {
                    try {
                        if (floatingWidgetsBinding[J].root.isShown) {
                            layoutParams.set(J,
                                    functionsClassWidgets.handleOrientationWidgetPortrait("${appWidgetId[J]}" + appWidgetProviderInfo[J].provider.packageName))

                            windowManager.updateViewLayout(floatingWidgetsBinding[J].root, layoutParams[J])
                        }
                    } catch (e: WindowManager.InvalidDisplayException) {
                        e.printStackTrace()
                    }
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {

                for (J in 0 until floatingWidgetsBinding.size) {
                    try {
                        if (floatingWidgetsBinding[J].root.isShown) {
                            layoutParams.set(J,
                                    functionsClassWidgets.handleOrientationWidgetLandscape("${appWidgetId[J]}" + appWidgetProviderInfo[J].provider.packageName))

                            windowManager.updateViewLayout(floatingWidgetsBinding[J].root, layoutParams[J])
                        }
                    } catch (e: WindowManager.InvalidDisplayException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onStartCommand(intent: Intent?, flags: Int, serviceStartId: Int): Int {
        super.onStartCommand(intent, flags, serviceStartId)

        intent?.run {

            if (this@run.hasExtra(getString(R.string.remove_all_floatings))) {
                if (this@run.getStringExtra(getString(R.string.remove_all_floatings)) == getString(R.string.remove_all_floatings)) {
                    for (removeCount in 0 until floatingWidgetsBinding.size) {
                        try {
                            if (floatingWidgetsBinding[removeCount].root.isShown) {
                                try {
                                    windowManager.removeView(floatingWidgetsBinding[removeCount].root)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1

                                    if (PublicVariable.allFloatingCounter == 0) {
                                        if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                                            stopService(Intent(applicationContext, BindServices::class.java))
                                        }
                                    }
                                }
                            } else if (PublicVariable.allFloatingCounter == 0) {

                                if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                                    stopService(Intent(applicationContext, BindServices::class.java))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    PublicVariable.floatingWidgetsIdList.clear()
                    PublicVariable.floatingWidgetsCounter = -1

                    stopSelf()

                    return START_NOT_STICKY
                }
            }

            val startId = startIdCounter.size
            startIdCounter.add(startId)

            floatingWidgetsBinding.add(startId, FloatingWidgetsBinding.inflate(layoutInflater))

            widgetLayout.add(startId, floatingWidgetsBinding[startId].widgetViewGroup)
            wholeViewWidget.add(startId, floatingWidgetsBinding[startId].wholeViewWidget)
            widgetLabel.add(startId, floatingWidgetsBinding[startId].widgetLabel)
            widgetMoveButton.add(startId, floatingWidgetsBinding[startId].widgetMoveButton)
            widgetCloseButton.add(startId, floatingWidgetsBinding[startId].widgetCloseButton)
            widgetResizeControl.add(startId, floatingWidgetsBinding[startId].widgetResizeControl)

            appWidgetId.add(startId, this@run.getIntExtra("WidgetId", -1))

            appWidgetManager.getAppWidgetInfo(appWidgetId[startId])?.let {

                appWidgetProviderInfo.add(startId, it)

                appWidgetHosts.add(startId, AppWidgetHost(applicationContext, System.currentTimeMillis().toInt()))
                appWidgetHosts[startId].startListening()

                if (PublicVariable.themeLightDark) {
                    wholeViewWidget[startId].backgroundTintList = ColorStateList.valueOf(getColor(R.color.light))
                    widgetLabel[startId].setBackgroundColor(getColor(R.color.light_transparent_high))
                    widgetLabel[startId].setTextColor(getColor(R.color.dark))
                } else {
                    wholeViewWidget[startId].backgroundTintList = ColorStateList.valueOf(getColor(R.color.dark))
                    widgetLabel[startId].setBackgroundColor(getColor(R.color.dark_transparent_high))
                    widgetLabel[startId].setTextColor(getColor(R.color.light))
                }

                widgetColor.add(startId, functionsClassLegacy.extractVibrantColor(appWidgetProviderInfo[startId].loadIcon(applicationContext, DisplayMetrics.DENSITY_LOW)))

                if (this@run.hasExtra("WidgetLabel")) {
                    val widgetLabelText = this@run.getStringExtra("WidgetLabel")
                    widgetLabel[startId].text = widgetLabelText
                } else {
                    val widgetLabelText = if (appWidgetProviderInfo[startId].loadLabel(packageManager) == null) {
                        getString(R.string.widgetHint)
                    } else {
                        appWidgetProviderInfo[startId].loadLabel(packageManager)
                    }
                    widgetLabel[startId].text = widgetLabelText
                }
                widgetLabel[startId].setTextColor(widgetColor[startId])


                val moveLayerDrawable = getDrawable(R.drawable.draw_move) as LayerDrawable?
                val moveBackgroundLayerDrawable = moveLayerDrawable?.findDrawableByLayerId(R.id.backgroundTemporary)
                moveBackgroundLayerDrawable?.setTint(widgetColor[startId])

                val closeLayerDrawable = getDrawable(R.drawable.draw_close_service) as LayerDrawable?
                val closeBackgroundLayerDrawable = closeLayerDrawable?.findDrawableByLayerId(R.id.backgroundTemporary)
                closeBackgroundLayerDrawable?.setTint(widgetColor[startId])

                val resizeDrawable = getDrawable(R.drawable.w_resize)!!.mutate()
                resizeDrawable.setTint(widgetColor[startId])

                widgetMoveButton[startId].setImageDrawable(moveLayerDrawable)
                widgetCloseButton[startId].setImageDrawable(closeLayerDrawable)
                widgetResizeControl[startId].background = resizeDrawable

                appWidgetHostView.add(startId, appWidgetHosts[startId].createView(applicationContext, appWidgetId[startId], appWidgetProviderInfo[startId]))

                val initWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 213f, resources.displayMetrics).toInt()
                val initHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 133f, resources.displayMetrics).toInt()

                val widgetBundleConfiguration = Bundle()
                widgetBundleConfiguration.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, initWidth)
                widgetBundleConfiguration.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, initHeight)
                widgetBundleConfiguration.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, functionsClassLegacy.displayX() / 2)
                widgetBundleConfiguration.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, functionsClassLegacy.displayY() / 2)
                appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId[startId], appWidgetProviderInfo[startId].provider, widgetBundleConfiguration)

                appWidgetHostView[startId].setAppWidget(appWidgetId[startId], appWidgetProviderInfo[startId])
                appWidgetHostView[startId].minimumHeight = initHeight
                appWidgetHostView[startId].minimumWidth = initWidth

                widgetLayout[startId].addView(appWidgetHostView[startId])

                val widgetRelativeLayout = RelativeLayout.LayoutParams(initWidth, initHeight)
                wholeViewWidget[startId].layoutParams = widgetRelativeLayout
                wholeViewWidget[startId].requestLayout()

                layoutParams.add(startId,
                        functionsClassLegacy.normalWidgetLayoutParams(appWidgetProviderInfo[startId].provider.packageName,
                                appWidgetId[startId],
                                initWidth, initHeight))

                try {
                    floatingWidgetsBinding[startId].root.tag = startId
                    windowManager.addView(floatingWidgetsBinding[startId].root, layoutParams[startId])
                } catch (e: WindowManager.InvalidDisplayException) {
                    e.printStackTrace()
                }

                widgetLabel[startId].setOnClickListener {

                }
                widgetLabel[startId].setOnTouchListener(object : View.OnTouchListener {

                    var initialX: Int = 0
                    var initialY: Int = 0

                    var initialTouchX: Float = 0f
                    var initialTouchY: Float = 0f

                    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                        val layoutParamsTouch = layoutParams[startId]

                        when (motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> {

                                initialX = layoutParamsTouch.x
                                initialY = layoutParamsTouch.y

                                initialTouchX = motionEvent.rawX
                                initialTouchY = motionEvent.rawY

                            }
                            MotionEvent.ACTION_UP -> {

                                layoutParamsTouch.x = initialX + ((motionEvent.rawX - initialTouchX)).toInt()
                                layoutParamsTouch.y = initialY + ((motionEvent.rawY - initialTouchY)).toInt()

                                functionsClassLegacy.savePreference(appWidgetId[startId].toString() + appWidgetProviderInfo[startId].provider.packageName,
                                        "X", layoutParamsTouch.x)
                                functionsClassLegacy.savePreference(appWidgetId[startId].toString() + appWidgetProviderInfo[startId].provider.packageName,
                                        "Y", layoutParamsTouch.y)
                            }
                            MotionEvent.ACTION_MOVE -> {

                                layoutParamsTouch.x = initialX + ((motionEvent.rawX - initialTouchX)).toInt()
                                layoutParamsTouch.y = initialY + ((motionEvent.rawY - initialTouchY)).toInt()

                                try {
                                    windowManager.updateViewLayout(floatingWidgetsBinding[startId].root, layoutParamsTouch)
                                } catch (e: WindowManager.InvalidDisplayException) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        return false
                    }
                })

                widgetMoveButton[startId].setOnClickListener {

                }
                widgetMoveButton[startId].setOnTouchListener(object : View.OnTouchListener {

                    var initialX: Int = 0
                    var initialY: Int = 0

                    var initialTouchX: Float = 0f
                    var initialTouchY: Float = 0f

                    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                        val layoutParamsTouch = layoutParams[startId]

                        when (motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> {

                                initialX = layoutParamsTouch.x
                                initialY = layoutParamsTouch.y

                                initialTouchX = motionEvent.rawX
                                initialTouchY = motionEvent.rawY
                            }
                            MotionEvent.ACTION_UP -> {

                                layoutParamsTouch.x = (initialX + (motionEvent.rawX - initialTouchX)).toInt()
                                layoutParamsTouch.y = (initialY + (motionEvent.rawY - initialTouchY)).toInt()

                                functionsClassLegacy.savePreference(appWidgetId[startId].toString() + appWidgetProviderInfo[startId].provider.packageName,
                                        "X", layoutParamsTouch.x)
                                functionsClassLegacy.savePreference(appWidgetId[startId].toString() + appWidgetProviderInfo[startId].provider.packageName,
                                        "Y", layoutParamsTouch.y)
                            }
                            MotionEvent.ACTION_MOVE -> {

                                layoutParamsTouch.x = (initialX + (motionEvent.rawX - initialTouchX)).toInt()
                                layoutParamsTouch.y = (initialY + (motionEvent.rawY - initialTouchY)).toInt()

                                try {
                                    windowManager.updateViewLayout(floatingWidgetsBinding[startId].root, layoutParamsTouch)
                                } catch (e: WindowManager.InvalidDisplayException) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        return false
                    }
                })

                widgetCloseButton[startId].setOnClickListener {

                    try {
                        windowManager.removeView(floatingWidgetsBinding[startId].root)
                    } finally {
                        PublicVariable.floatingWidgetsIdList.remove(appWidgetId[startId] as Any)
                        PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1
                        PublicVariable.floatingWidgetsCounter = PublicVariable.floatingWidgetsCounter - 1

                        if (PublicVariable.allFloatingCounter == 0) {

                            if (!PreferenceManager.getDefaultSharedPreferences(applicationContext)
                                            .getBoolean("stable", true)) {

                                stopService(Intent(applicationContext, BindServices::class.java))
                            }
                            stopSelf()
                        }
                    }
                }

                widgetResizeControl[startId].setOnTouchListener(object : View.OnTouchListener {

                    var initialWidth: Int = 0
                    var initialHeight: Int = 0

                    var initialTouchX: Float = 0f
                    var initialTouchY: Float = 0f

                    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                        val layoutParamsTouch = layoutParams[startId]

                        when (motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> {

                                initialWidth = layoutParamsTouch.width
                                initialHeight = layoutParamsTouch.height

                                initialTouchX = motionEvent.rawX
                                initialTouchY = motionEvent.rawY
                            }
                            MotionEvent.ACTION_UP ->

                                if (layoutParamsTouch.width < initWidth || layoutParamsTouch.height < initHeight) {

                                } else {
                                    functionsClassLegacy.savePreference(appWidgetId[startId].toString() + appWidgetProviderInfo[startId].provider.packageName,
                                            "WidgetWidth", layoutParamsTouch.width)
                                    functionsClassLegacy.savePreference(appWidgetId[startId].toString() + appWidgetProviderInfo[startId].provider.packageName,
                                            "WidgetHeight", layoutParamsTouch.height)
                                }
                            MotionEvent.ACTION_MOVE -> {

                                val xWidthMove = (initialWidth + (motionEvent.rawX - initialTouchX)).toInt()
                                val yHeightMove = (initialHeight + (motionEvent.rawY - initialTouchY)).toInt()

                                layoutParamsTouch.width = xWidthMove
                                layoutParamsTouch.height = yHeightMove

                                appWidgetHostView[startId].updateAppWidgetSize(Bundle(), layoutParamsTouch.width, layoutParamsTouch.height, layoutParamsTouch.width, layoutParamsTouch.height)
                                windowManager.updateViewLayout(floatingWidgetsBinding[startId].root, layoutParamsTouch)
                            }
                        }

                        return false
                    }
                })
            }
        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        functionsClassLegacy = FunctionsClassLegacy(applicationContext)
        functionsClassWidgets = FunctionsClassWidgets(applicationContext)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        appWidgetManager = AppWidgetManager.getInstance(applicationContext)
    }
}