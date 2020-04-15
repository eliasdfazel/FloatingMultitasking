/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/15/20 3:28 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FloatingServices

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.IBinder
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Utils.FloatingFoldersUtils
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.imageview.customshapes.ShapesImage

class FloatingFoldersXYZ : Service() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val floatingFoldersUtils: FloatingFoldersUtils by lazy {
        FloatingFoldersUtils()
    }

    private lateinit var windowManager: WindowManager

    private val layoutParams: ArrayList<WindowManager.LayoutParams> = ArrayList<WindowManager.LayoutParams>()
    private val stickyEdgeParams: ArrayList<WindowManager.LayoutParams> = ArrayList<WindowManager.LayoutParams>()

    private val pinIndicatorView: ArrayList<ShapesImage> = ArrayList<ShapesImage>()
    private val notificationDotView: ArrayList<ShapesImage> = ArrayList<ShapesImage>()

    private object XY {
        var xPosition = 0
        var yPosition: Int = 0

        var xInitial: Int = 13
        var yInitial: Int = 13

        var xMove: Int = 0
        var yMove: Int = 0
    }

    private val folderName: ArrayList<String> = ArrayList<String>()

    private val movePermit: ArrayList<Boolean> = ArrayList<Boolean>()
    private val touchingDelay:  ArrayList<Boolean> = ArrayList<Boolean>()
    private val stickedToEdge: ArrayList<Boolean> = ArrayList<Boolean>()
    private val openPermit: ArrayList<Boolean> = ArrayList<Boolean>()

    lateinit var notificationPackage: String
    var showNotificationDot = false

    private val mapContentFolderName: HashMap<String, String> = HashMap<String, String>()
    private val mapFolderNameStartId: HashMap<String, Int> = HashMap<String, Int>()

    lateinit var runnablePressHold: Runnable
    private var handlerPressHold: Handler = Handler()

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    private val simpleOnGestureListener: ArrayList<SimpleOnGestureListener> = ArrayList<SimpleOnGestureListener>()
    private val gestureDetector: ArrayList<GestureDetector> = ArrayList<GestureDetector>()

    private val flingAnimationX: ArrayList<FlingAnimation> = ArrayList<FlingAnimation>()
    private val flingAnimationY: ArrayList<FlingAnimation> = ArrayList<FlingAnimation>()

    var flingPositionX: Float = 0f
    var flingPositionY: Float = 0f

    private val layoutInflater: LayoutInflater by lazy {
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    private val floatingView: ArrayList<ViewGroup> = ArrayList<ViewGroup>()

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {

                for (J in 0 until floatingView.size) {
                    try {
                        if (floatingView[J].isShown) {
                            layoutParams.set(J,
                                    functionsClass.handleOrientationPortrait(folderName[J], layoutParams[J].height))

                            windowManager.updateViewLayout(floatingView[J], layoutParams[J])
                        }
                    } catch (e: WindowManager.InvalidDisplayException) {
                        e.printStackTrace()
                    }
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {

                for (J in 0 until floatingView.size) {
                    try {
                        if (floatingView[J].isShown) {
                            layoutParams.set(J,
                                    functionsClass.handleOrientationLandscape(folderName[J], layoutParams[J].height))

                            windowManager.updateViewLayout(floatingView[J], layoutParams[J])
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

    override fun onStartCommand(intent: Intent?, flags: Int, serviceStartId: Int): Int {
        super.onStartCommand(intent, flags, serviceStartId)
        FunctionsClassDebug.PrintDebug(this@FloatingFoldersXYZ.javaClass.simpleName + " ::: StartId ::: " + serviceStartId)

        val startId = (serviceStartId - 1)

        intent?.run {

            if (this@run.hasExtra(getString(R.string.remove_all_floatings))) {
                if (this@run.getStringExtra(getString(R.string.remove_all_floatings)) == getString(R.string.remove_all_floatings)) {

                    for (removeCount in 0 until floatingView.size) {
                        try {
                            if (floatingView[removeCount].isShown) {
                                try {
                                    windowManager.removeView(floatingView[removeCount])
                                } catch (e: java.lang.Exception) {
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

                    PublicVariable.floatingFoldersList.clear()
                    FloatingFoldersUtils.floatingFoldersCounterType[this@FloatingFoldersXYZ.javaClass.simpleName] = -1

                    stopSelf()
                }
            }

            folderName.add(startId, intent.getStringExtra("folderName"))

            touchingDelay.add(startId, false)
            stickedToEdge.add(startId, false)

            movePermit.add(startId, true)
            openPermit.add(startId, true)

            var folderSize: Int = 36

            if (PublicVariable.size == 13 || PublicVariable.size == 26) {//Small
                floatingView.add(startId, layoutInflater.inflate(R.layout.floating_folder_small, null, false) as ViewGroup)

                folderSize = 24
            } else if (PublicVariable.size == 39 || PublicVariable.size == 52) {//Medium
                floatingView.add(startId, layoutInflater.inflate(R.layout.floating_folder_medium, null, false) as ViewGroup)

                folderSize = 36
            } else if (PublicVariable.size == 65 || PublicVariable.size == 78) {//Large
                floatingView.add(startId, layoutInflater.inflate(R.layout.floating_folder_large, null, false) as ViewGroup)

                folderSize = 48
            } else {
                floatingView.add(startId, layoutInflater.inflate(R.layout.floating_folder_medium, null, false) as ViewGroup)

                folderSize = 36
            }

            var folderBackgroundDrawable: Drawable? = null
            when (functionsClass.shapesImageId()) {
                1 -> folderBackgroundDrawable = getDrawable(R.drawable.category_droplet_icon)
                2 -> folderBackgroundDrawable = getDrawable(R.drawable.category_circle_icon)
                3 -> folderBackgroundDrawable = getDrawable(R.drawable.category_square_icon)
                4 -> folderBackgroundDrawable = getDrawable(R.drawable.category_squircle_icon)
                0 -> folderBackgroundDrawable = null
            }
            if (folderBackgroundDrawable != null) {
                folderBackgroundDrawable.setTint(PublicVariable.primaryColor)
                folderBackgroundDrawable.alpha = functionsClass.readDefaultPreference("autoTrans", 255)
            }
            floatingView[startId].background = folderBackgroundDrawable

            val bottomRight = functionsClass.initShapesImage(floatingView[startId], R.id.bottomRight)
            val topLeft = functionsClass.initShapesImage(floatingView[startId], R.id.topLeft)
            val bottomLeft = functionsClass.initShapesImage(floatingView[startId], R.id.bottomLeft)
            val topRight = functionsClass.initShapesImage(floatingView[startId], R.id.topRight)

            pinIndicatorView.add(startId, functionsClass.initShapesImage(floatingView[startId], R.id.pinIndicatorView))
            notificationDotView.add(startId, functionsClass.initShapesImage(floatingView[startId], if (functionsClass.checkStickyEdge()) {
                R.id.notificationDotEnd
            } else {
                R.id.notificationDotStart
            }))












        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}