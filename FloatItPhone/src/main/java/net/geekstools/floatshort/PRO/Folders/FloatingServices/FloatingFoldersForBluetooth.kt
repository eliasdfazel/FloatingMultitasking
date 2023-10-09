/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/11/20 10:43 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FloatingServices

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.TypedValue
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.WindowManager.BadTokenException
import android.view.accessibility.AccessibilityEvent
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Utils.FloatingFoldersUtils
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Utils.FloatingFoldersUtils.FloatingFoldersCounterType.floatingFoldersCounterType
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.Debug
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.imageview.customshapes.ShapesImage
import kotlin.math.abs

class FloatingFoldersForBluetooth : Service() {

    private val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }
    private val fileIO: FileIO by lazy {
        FileIO(applicationContext)
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

    lateinit var alreadyNotificationPackage: String
    private var showNotificationDot: Boolean = false

    private val mapContentFolderName: HashMap<String, String> = HashMap<String, String>()
    private val mapFolderNameStartId: HashMap<String, Int> = HashMap<String, Int>()

    lateinit var runnablePressHold: Runnable
    private var handlerPressHold: Handler = Handler(Looper.getMainLooper())

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClassLegacy.customIconPackageName())
    }

    private val simpleOnGestureListener: ArrayList<SimpleOnGestureListener> = ArrayList<SimpleOnGestureListener>()
    private val gestureDetector: ArrayList<GestureDetector> = ArrayList<GestureDetector>()

    private val flingAnimationX: ArrayList<FlingAnimation> = ArrayList<FlingAnimation>()
    private val flingAnimationY: ArrayList<FlingAnimation> = ArrayList<FlingAnimation>()

    var flingPositionX: Float = 0f
    var flingPositionY: Float = 0f

    private val startIdCounter: ArrayList<Int> = ArrayList<Int>()

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
                                    functionsClassLegacy.handleOrientationPortrait(folderName[J], layoutParams[J].height))

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
                                    functionsClassLegacy.handleOrientationLandscape(folderName[J], layoutParams[J].height))

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
        Debug.PrintDebug(this@FloatingFoldersForBluetooth.javaClass.simpleName + " ::: StartId ::: " + serviceStartId)

        intent?.run {

            if (this@run.hasExtra(getString(R.string.remove_all_floatings))) {
                if (this@run.getStringExtra(getString(R.string.remove_all_floatings)) == getString(R.string.remove_all_floatings)) {

                    for (removeCount in 0 until floatingView.size) {
                        try {
                            if (floatingView[removeCount].isShown) {
                                try {
                                    windowManager.removeView(floatingView[removeCount])
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

                    PublicVariable.floatingFoldersList.clear()
                    FloatingFoldersUtils.floatingFoldersCounterType[this@FloatingFoldersForBluetooth.javaClass.simpleName] = -1

                    stopSelf()
                }

                return Service.START_NOT_STICKY
            }

            val startId = startIdCounter.size
            startIdCounter.add(startId)

            folderName.add(startId, intent.getStringExtra("folderName")!!)

            touchingDelay.add(startId, false)
            stickedToEdge.add(startId, false)

            movePermit.add(startId, true)
            openPermit.add(startId, true)

            var folderSize: Int = 36

            if (PublicVariable.floatingSizeNumber == 13 || PublicVariable.floatingSizeNumber == 26) {//Small
                floatingView.add(startId, layoutInflater.inflate(R.layout.floating_folder_small, null, false) as ViewGroup)

                folderSize = 24
            } else if (PublicVariable.floatingSizeNumber == 39 || PublicVariable.floatingSizeNumber == 52) {//Medium
                floatingView.add(startId, layoutInflater.inflate(R.layout.floating_folder_medium, null, false) as ViewGroup)

                folderSize = 36
            } else if (PublicVariable.floatingSizeNumber == 65 || PublicVariable.floatingSizeNumber == 78) {//Large
                floatingView.add(startId, layoutInflater.inflate(R.layout.floating_folder_large, null, false) as ViewGroup)

                folderSize = 48
            } else {
                floatingView.add(startId, layoutInflater.inflate(R.layout.floating_folder_medium, null, false) as ViewGroup)

                folderSize = 36
            }

            var folderBackgroundDrawable: Drawable? = null
            when (functionsClassLegacy.shapesImageId()) {
                1 -> folderBackgroundDrawable = getDrawable(R.drawable.category_droplet_icon)
                2 -> folderBackgroundDrawable = getDrawable(R.drawable.category_circle_icon)
                3 -> folderBackgroundDrawable = getDrawable(R.drawable.category_square_icon)
                4 -> folderBackgroundDrawable = getDrawable(R.drawable.category_squircle_icon)
                0 -> folderBackgroundDrawable = null
            }
            if (folderBackgroundDrawable != null) {
                folderBackgroundDrawable.setTint(PublicVariable.primaryColor)
                folderBackgroundDrawable.alpha = functionsClassLegacy.readDefaultPreference("autoTrans", 255)
            }
            floatingView[startId].background = folderBackgroundDrawable

            val bottomRight = functionsClassLegacy.initShapesImage(floatingView[startId], R.id.bottomRight)
            val topLeft = functionsClassLegacy.initShapesImage(floatingView[startId], R.id.topLeft)
            val bottomLeft = functionsClassLegacy.initShapesImage(floatingView[startId], R.id.bottomLeft)
            val topRight = functionsClassLegacy.initShapesImage(floatingView[startId], R.id.topRight)

            pinIndicatorView.add(startId, functionsClassLegacy.initShapesImage(floatingView[startId], R.id.pinIndicatorView))
            notificationDotView.add(startId, functionsClassLegacy.initShapesImage(floatingView[startId], if (functionsClassLegacy.checkStickyEdge()) {
                R.id.notificationDotEnd
            } else {
                R.id.notificationDotStart
            }))

            mapFolderNameStartId[folderName[startId]] = startId

            val appsInFolder = fileIO.readFileLinesAsArray(folderName[startId])
            if (appsInFolder != null) {

                if (appsInFolder.isNotEmpty()) {
                    for (i in appsInFolder.indices) {
                        mapContentFolderName[appsInFolder[i]] = folderName[startId]

                        if (getFileStreamPath(appsInFolder[i].toString() + "_" + "Notification" + "Package").exists()) {
                            showNotificationDot = true
                            alreadyNotificationPackage = appsInFolder[i]
                        }
                    }
                }

                //bottomRight
                try {

                    bottomRight.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) {
                        loadCustomIcons.getDrawableIconForPackage(appsInFolder[0], functionsClassLegacy.shapedAppIcon(appsInFolder[0]))
                    } else {
                        functionsClassLegacy.shapedAppIcon(appsInFolder[0])
                    })
                    bottomRight.imageAlpha = functionsClassLegacy.readDefaultPreference("autoTrans", 255)

                } catch (e: Exception) {
                    bottomRight.setImageDrawable(null)
                }

                //topLeft
                try {
                    topLeft.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) {
                        loadCustomIcons.getDrawableIconForPackage(appsInFolder[1], functionsClassLegacy.shapedAppIcon(appsInFolder[1]))
                    } else {
                        functionsClassLegacy.shapedAppIcon(appsInFolder[1])
                    })
                    topLeft.imageAlpha = functionsClassLegacy.readDefaultPreference("autoTrans", 255)

                } catch (e: Exception) {
                    topLeft.setImageDrawable(null)
                }

                //bottomLeft
                try {

                    bottomLeft.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) {
                        loadCustomIcons.getDrawableIconForPackage(appsInFolder[2], functionsClassLegacy.shapedAppIcon(appsInFolder[2]))
                    } else {
                        functionsClassLegacy.shapedAppIcon(appsInFolder[2])
                    })
                    bottomLeft.imageAlpha = functionsClassLegacy.readDefaultPreference("autoTrans", 255)

                } catch (e: Exception) {
                    bottomLeft.setImageDrawable(null)
                }

                //topRight
                try {
                    topRight.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(appsInFolder[3], functionsClassLegacy.shapedAppIcon(appsInFolder[3])) else functionsClassLegacy.shapedAppIcon(appsInFolder[3]))
                    topRight.imageAlpha = functionsClassLegacy.readDefaultPreference("autoTrans", 255)
                } catch (e: Exception) {
                    topRight.setImageDrawable(null)
                }
            }


            val HW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (folderSize * 2).toFloat(), resources.displayMetrics).toInt()

            val nameForPosition = folderName[startId]
            val sharedPreferencesPosition = getSharedPreferences(nameForPosition, Context.MODE_PRIVATE)

            XY.xInitial = XY.xInitial + 13
            XY.yInitial = XY.yInitial + 13

            XY.xPosition = sharedPreferencesPosition.getInt("X", XY.xInitial)
            XY.yPosition = sharedPreferencesPosition.getInt("Y", XY.yInitial)

            layoutParams.add(startId, functionsClassLegacy.normalLayoutParams(HW, XY.xPosition, XY.yPosition))

            try {
                floatingView[startId].tag = startId
                windowManager.addView(floatingView[startId], layoutParams[startId])
            } catch (e: BadTokenException) {
                e.printStackTrace()
            }

            XY.xMove = XY.xPosition
            XY.yMove = XY.yPosition

            if (!functionsClassLegacy.litePreferencesEnabled()) {
                flingAnimationX.add(startId, FlingAnimation(FloatValueHolder())
                        .setFriction(functionsClassLegacy.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f)))

                flingAnimationX[startId].setMaxValue((functionsClassLegacy.displayX() - folderSize).toFloat())
                flingAnimationX[startId].setMinValue(0f)

                flingAnimationY.add(startId, FlingAnimation(FloatValueHolder())
                        .setFriction(functionsClassLegacy.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f)))

                flingAnimationY[startId].setMaxValue((functionsClassLegacy.displayY() - folderSize).toFloat())
                flingAnimationY[startId].setMinValue(0f)

                simpleOnGestureListener.add(startId, object : SimpleOnGestureListener() {
                    override fun onFling(motionEventFirst: MotionEvent?, motionEventLast: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

                        if (movePermit[startId]) {

                            flingAnimationX[startId].setStartVelocity(velocityX)
                            flingAnimationY[startId].setStartVelocity(velocityY)

                            flingAnimationX[startId].setStartValue(flingPositionX)
                            flingAnimationY[startId].setStartValue(flingPositionY)

                            try {
                                flingAnimationX[startId].addUpdateListener { animation, value, velocity ->

                                    if (floatingView[startId].isShown) {
                                        layoutParams[startId].x = value.toInt() // X movePoint
                                        windowManager.updateViewLayout(floatingView[startId], layoutParams[startId])

                                        XY.xMove = value.toInt()
                                    }
                                }

                                flingAnimationY[startId].addUpdateListener { animation, value, velocity ->

                                    if (floatingView[startId].isShown) {
                                        layoutParams[startId].y = value.toInt() // Y movePoint
                                        windowManager.updateViewLayout(floatingView[startId], layoutParams[startId])

                                        XY.yMove = value.toInt()
                                    }
                                }

                                flingAnimationX[startId].start()
                                flingAnimationY[startId].start()

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            openPermit[startId] = false
                        }
                        return false
                    }
                })

                flingAnimationX[startId].addEndListener { animation, canceled, value, velocity ->

                    openPermit[startId] = true
                }
                flingAnimationY[startId].addEndListener { animation, canceled, value, velocity ->

                    openPermit[startId] = true
                }

                gestureDetector.add(startId, GestureDetector(applicationContext, simpleOnGestureListener[startId]))
            }

            floatingView[startId].setOnTouchListener(object : View.OnTouchListener {

                var initialX: Int = 0
                var initialY: Int = 0

                var initialTouchX: Float = 0f
                var initialTouchY: Float = 0f

                override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

                    try {
                        flingAnimationX[startId].cancel()
                        flingAnimationY[startId].cancel()

                        gestureDetector[startId].onTouchEvent(motionEvent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val layoutParamsOnTouch: WindowManager.LayoutParams

                    if (stickedToEdge[startId]) {
                        layoutParamsOnTouch = stickyEdgeParams[startId]
                        layoutParamsOnTouch.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    } else {
                        layoutParamsOnTouch = layoutParams[startId]
                    }

                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {

                            initialX = layoutParamsOnTouch.x
                            initialY = layoutParamsOnTouch.y

                            initialTouchX = motionEvent.rawX
                            initialTouchY = motionEvent.rawY

                            XY.xMove = layoutParamsOnTouch.x
                            XY.yMove = layoutParamsOnTouch.y

                            touchingDelay[startId] = true
                            runnablePressHold = Runnable {

                                if (touchingDelay[startId]) {

                                    functionsClassLegacy.PopupOptionFolder(
                                            view,
                                            folderName[startId],
                                            this@FloatingFoldersForBluetooth.javaClass.simpleName,
                                            startId,
                                            initialX,
                                            initialY + PublicVariable.statusBarHeight
                                    )

                                    openPermit[startId] = false
                                }
                            }

                            handlerPressHold.postDelayed(runnablePressHold, functionsClassLegacy.readDefaultPreference("delayPressHold", 333).toLong())

                        }
                        MotionEvent.ACTION_UP -> {

                            touchingDelay[startId] = false
                            handlerPressHold.removeCallbacks(runnablePressHold)

                            Handler(Looper.getMainLooper()).postDelayed({
                                openPermit[startId] = true
                            }, 130)

                            if (movePermit[startId]) {

                                layoutParamsOnTouch.x = initialX + (motionEvent.rawX - initialTouchX).toInt()
                                layoutParamsOnTouch.y = initialY + (motionEvent.rawY - initialTouchY).toInt()

                                XY.xMove = layoutParamsOnTouch.x
                                XY.yMove = layoutParamsOnTouch.y

                                val nameForPositionNew = folderName[startId]
                                val sharedPrefPosition = getSharedPreferences(nameForPositionNew, Context.MODE_PRIVATE)
                                val editor = sharedPrefPosition.edit()
                                editor.putInt("X", layoutParamsOnTouch.x)
                                editor.putInt("Y", layoutParamsOnTouch.y)
                                editor.apply()

                            } else {

                                if (!functionsClassLegacy.litePreferencesEnabled()) {

                                    var initialTouchXBoundBack = getSharedPreferences(folderName[startId], Context.MODE_PRIVATE).getInt("X", 0).toFloat()
                                    if (initialTouchXBoundBack < 0) {
                                        initialTouchXBoundBack = 0f
                                    } else if (initialTouchXBoundBack > functionsClassLegacy.displayX()) {
                                        initialTouchXBoundBack = functionsClassLegacy.displayX().toFloat()
                                    }

                                    var initialTouchYBoundBack = getSharedPreferences(folderName[startId], Context.MODE_PRIVATE).getInt("Y", 0).toFloat()
                                    if (initialTouchYBoundBack < 0) {
                                        initialTouchYBoundBack = 0f
                                    } else if (initialTouchYBoundBack > functionsClassLegacy.displayY()) {
                                        initialTouchYBoundBack = functionsClassLegacy.displayY().toFloat()
                                    }

                                    val springForceX = SpringForce()
                                            .setFinalPosition(initialTouchXBoundBack) //EDIT HERE
                                            .setStiffness(SpringForce.STIFFNESS_MEDIUM)
                                            .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)

                                    val springForceY = SpringForce()
                                            .setFinalPosition(initialTouchYBoundBack) //EDIT HERE
                                            .setStiffness(SpringForce.STIFFNESS_MEDIUM)
                                            .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)

                                    val springAnimationX = SpringAnimation(FloatValueHolder())
                                            .setMinValue(0f)
                                            .setSpring(springForceX)

                                    val springAnimationY = SpringAnimation(FloatValueHolder())
                                            .setMinValue(0f)
                                            .setSpring(springForceY)

                                    var springStartValueX = motionEvent.rawX
                                    if (springStartValueX < 0f) {
                                        springStartValueX = 0f
                                    } else if (springStartValueX > functionsClassLegacy.displayX()) {
                                        springStartValueX = functionsClassLegacy.displayX().toFloat()
                                    }

                                    var springStartValueY = motionEvent.rawY
                                    if (springStartValueY < 0f) {
                                        springStartValueY = 0f
                                    } else if (springStartValueY > functionsClassLegacy.displayY()) {
                                        springStartValueY = functionsClassLegacy.displayY().toFloat()
                                    }

                                    springAnimationX.setStartValue(springStartValueX)
                                    springAnimationX.setStartVelocity(-0f)
                                    springAnimationX.setMaxValue(functionsClassLegacy.displayX().toFloat())

                                    springAnimationY.setStartValue(springStartValueY)
                                    springAnimationY.setStartVelocity(-0f)
                                    springAnimationY.setMaxValue(functionsClassLegacy.displayY().toFloat())

                                    springAnimationX.addUpdateListener { animation, value, velocity ->

                                        if (floatingView[startId].isShown) {
                                            layoutParamsOnTouch.x = value.toInt() // X movePoint
                                            windowManager.updateViewLayout(floatingView[startId], layoutParamsOnTouch)
                                        }
                                    }
                                    springAnimationY.addUpdateListener { animation, value, velocity ->

                                        if (floatingView[startId].isShown) {
                                            layoutParamsOnTouch.y = value.toInt() // Y movePoint
                                            windowManager.updateViewLayout(floatingView[startId], layoutParamsOnTouch)
                                        }
                                    }

                                    springAnimationX.addEndListener { animation, canceled, value, velocity -> openPermit[startId] = true }
                                    springAnimationY.addEndListener { animation, canceled, value, velocity -> openPermit[startId] = true }

                                    springAnimationX.start()
                                    springAnimationY.start()

                                }
                            }

                        }
                        MotionEvent.ACTION_MOVE -> {

                            if (movePermit[startId]) {

                                layoutParamsOnTouch.x = initialX + (motionEvent.rawX - initialTouchX).toInt()
                                layoutParamsOnTouch.y = initialY + (motionEvent.rawY - initialTouchY).toInt()

                                windowManager.updateViewLayout(floatingView[startId], layoutParamsOnTouch)

                                XY.xMove = layoutParamsOnTouch.x
                                XY.yMove = layoutParamsOnTouch.y

                                val difMoveX = (layoutParamsOnTouch.x - initialTouchX).toInt()
                                val difMoveY = (layoutParamsOnTouch.y - initialTouchY).toInt()

                                if (abs(difMoveX) > abs(PublicVariable.floatingViewsHW + PublicVariable.floatingViewsHW * 70 / 100)
                                        || abs(difMoveY) > abs(PublicVariable.floatingViewsHW + PublicVariable.floatingViewsHW * 70 / 100)) {

                                    openPermit[startId] = false
                                    touchingDelay[startId] = false

                                    handlerPressHold.removeCallbacks(runnablePressHold)
                                }

                                flingPositionX = layoutParamsOnTouch.x.toFloat()
                                flingPositionY = layoutParamsOnTouch.y.toFloat()

                            } else {
                                if (!functionsClassLegacy.litePreferencesEnabled()) {

                                    layoutParamsOnTouch.x = initialX + (motionEvent.rawX - initialTouchX).toInt() // X movePoint
                                    layoutParamsOnTouch.y = initialY + (motionEvent.rawY - initialTouchY).toInt() // Y movePoint

                                    windowManager.updateViewLayout(floatingView[startId], layoutParamsOnTouch)

                                    val difMoveX = (layoutParamsOnTouch.x - initialTouchX).toInt()
                                    val difMoveY = (layoutParamsOnTouch.y - initialTouchY).toInt()

                                    if (abs(difMoveX) > abs(PublicVariable.floatingViewsHW + PublicVariable.floatingViewsHW * 70 / 100)
                                            || abs(difMoveY) > abs(PublicVariable.floatingViewsHW + PublicVariable.floatingViewsHW * 70 / 100)) {

                                        sendBroadcast(Intent("Hide_PopupListView_Shortcuts" + applicationContext.getPackageName()))

                                        openPermit[startId] = false
                                        touchingDelay[startId] = false

                                        handlerPressHold.removeCallbacks(runnablePressHold)
                                    }
                                }
                            }

                        }
                    }

                    return false
                }
            })

            floatingView[startId].setOnClickListener { view ->

                if (openPermit[startId]) {

                    functionsClassLegacy.PopupAppListFolder(
                            view,
                            folderName[startId],
                            fileIO.readFileLinesAsArray(folderName[startId]),
                            this@FloatingFoldersForBluetooth.javaClass.simpleName,
                            startId,
                            XY.xMove,
                            XY.yMove,
                            layoutParams[startId].width
                    )
                }
            }

            notificationDotView[startId].setOnClickListener {

                functionsClassLegacy.PopupNotificationShortcuts(
                        notificationDotView[startId],
                        notificationDotView[startId].tag.toString(),
                        this@FloatingFoldersForBluetooth.javaClass.simpleName,
                        startId,
                        PublicVariable.primaryColor,
                        XY.xMove,
                        XY.yMove,
                        layoutParams[startId].width
                )
            }

            notificationDotView[startId].setOnLongClickListener { view ->

                if (functionsClassLegacy.AccessibilityServiceEnabled() && functionsClassLegacy.SettingServiceRunning(InteractionObserver::class.java)) {

                    functionsClassLegacy
                            .sendInteractionObserverEvent(view,
                                    notificationDotView[startId].tag.toString(),
                                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, 66666)

                } else {
                    try {
                        @SuppressLint("WrongConstant") val statusBarService = getSystemService("statusbar")
                        val statusBarManager = Class.forName("android.app.StatusBarManager")
                        val statuesBarInvocation = statusBarManager.getMethod("expandNotificationsPanel") //expandNotificationsPanel
                        statuesBarInvocation.invoke(statusBarService)
                    } catch (e: Exception) {
                        e.printStackTrace()

                        try {
                            @SuppressLint("WrongConstant") val statusBarService = getSystemService("statusbar")
                            val statusBarManager = Class.forName("android.app.StatusBarManager")
                            val statuesBarInvocation = statusBarManager.getMethod("expand") //expandNotificationsPanel
                            statuesBarInvocation.invoke(statusBarService)
                        } catch (e1: Exception) {
                            e1.printStackTrace()
                        }
                    }
                }

                true
            }

            if (serviceStartId == 1) {
                val floatingFolderClassInCommand: String = this@FloatingFoldersForBluetooth.javaClass.simpleName

                val intentFilter = IntentFilter()
                intentFilter.addAction("Split_Apps_Pair_$floatingFolderClassInCommand")
                intentFilter.addAction("Split_Apps_Single_$floatingFolderClassInCommand")
                intentFilter.addAction("Pin_App_$floatingFolderClassInCommand")
                intentFilter.addAction("Unpin_App_$floatingFolderClassInCommand")
                intentFilter.addAction("Remove_Category_$floatingFolderClassInCommand")
                intentFilter.addAction("Sticky_Edge${applicationContext.packageName}")
                intentFilter.addAction("Sticky_Edge_No${applicationContext.packageName}")
                intentFilter.addAction("Notification_Dot${applicationContext.packageName}")
                intentFilter.addAction("Notification_Dot_No${applicationContext.packageName}")
                val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

                    override fun onReceive(context: Context, intent: Intent) {

                        if (intent.action == "Split_Apps_Pair_$floatingFolderClassInCommand" && PublicVariable.splitScreen) {
                            PublicVariable.splitScreen = false

                            var packageNameSplitOne: String = "null"
                            var packageNameSplitTwo: String = "null"

                            if (getFileStreamPath(PublicVariable.splitPairPackage.toString() + ".SplitOne").exists()
                                    && getFileStreamPath(PublicVariable.splitPairPackage.toString() + ".SplitTwo").exists()) {

                                fileIO.readFile(PublicVariable.splitPairPackage.toString() + ".SplitOne")?.let {
                                    packageNameSplitOne = it
                                }
                                fileIO.readFile(PublicVariable.splitPairPackage.toString() + ".SplitTwo")?.let {
                                    packageNameSplitTwo = it
                                }

                            } else {

                                fileIO.readFileLinesAsArray(PublicVariable.splitPairPackage)?.get(0)?.let {
                                    packageNameSplitOne = it
                                }
                                fileIO.readFileLinesAsArray(PublicVariable.splitPairPackage)?.get(1)?.let {
                                    packageNameSplitTwo = it
                                }

                            }

                            Handler(Looper.getMainLooper()).postDelayed({
                                try {
                                    val splitOne = packageManager.getLaunchIntentForPackage(packageNameSplitOne)
                                    splitOne?.flags = Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK

                                    val splitTwo = packageManager.getLaunchIntentForPackage(packageNameSplitTwo)
                                    splitTwo?.flags = Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK

                                    splitOne?.let {

                                        startActivity(it)
                                    }

                                    Handler(Looper.getMainLooper()).postDelayed({
                                        splitTwo?.let {

                                            startActivity(it)
                                        }

                                        PublicVariable.splitScreen = true

                                        Handler(Looper.getMainLooper()).postDelayed({
                                            sendBroadcast(Intent("split_pair_finish" + getApplicationContext().getPackageName()))
                                        }, 700)

                                    }, 200)

                                    functionsClassLegacy.Toast(functionsClassLegacy.applicationName(packageNameSplitOne), Gravity.TOP)
                                    functionsClassLegacy.Toast(functionsClassLegacy.applicationName(packageNameSplitTwo), Gravity.BOTTOM)

                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                }
                            }, 700)
                        } else if (intent.action == "Split_Apps_Single_$floatingFolderClassInCommand" && PublicVariable.splitScreen) {
                            PublicVariable.splitScreen = false

                            Handler(Looper.getMainLooper()).postDelayed({
                                try {
                                    packageManager.getLaunchIntentForPackage(PublicVariable.splitSinglePackage)?.let {
                                        it.flags = Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK

                                        startActivity(it)
                                    }

                                    PublicVariable.splitScreen = true

                                    Handler(Looper.getMainLooper()).postDelayed({ sendBroadcast(Intent("split_single_finish" + getApplicationContext().getPackageName())) }, 500)

                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                }
                            }, 200)
                        } else if (intent.action == "Pin_App_$floatingFolderClassInCommand") {

                            movePermit[intent.getIntExtra("startId", 0)] = false

                            var folderBackgroundDrawablePin: Drawable? = null
                            when (functionsClassLegacy.shapesImageId()) {
                                1 -> {
                                    folderBackgroundDrawablePin = getDrawable(R.drawable.pin_droplet_icon)
                                    folderBackgroundDrawablePin?.setTint(context.getColor(R.color.red_transparent))
                                }
                                2 -> {
                                    folderBackgroundDrawablePin = getDrawable(R.drawable.pin_circle_icon)
                                    folderBackgroundDrawablePin?.setTint(context.getColor(R.color.red_transparent))
                                }
                                3 -> {
                                    folderBackgroundDrawablePin = getDrawable(R.drawable.pin_square_icon)
                                    folderBackgroundDrawablePin?.setTint(context.getColor(R.color.red_transparent))
                                }
                                4 -> {
                                    folderBackgroundDrawablePin = getDrawable(R.drawable.pin_squircle_icon)
                                    folderBackgroundDrawablePin?.setTint(context.getColor(R.color.red_transparent))
                                }
                                0 -> {
                                    folderBackgroundDrawablePin = getDrawable(R.drawable.pin_noshap)
                                    folderBackgroundDrawablePin?.setTint(context.getColor(R.color.red_transparent))
                                }
                            }

                            pinIndicatorView[intent.getIntExtra("startId", 0)].setImageDrawable(folderBackgroundDrawablePin)

                        } else if (intent.action == "Unpin_App_$floatingFolderClassInCommand") {

                            movePermit[intent.getIntExtra("startId", 0)] = true
                            pinIndicatorView[intent.getIntExtra("startId", 0)].setImageDrawable(null)

                        } else if (intent.action == "Remove_Category_$floatingFolderClassInCommand") {

                            if (floatingView[intent.getIntExtra("startId", 0)] == null) {
                                return
                            }

                            if (floatingView[intent.getIntExtra("startId", 0)].isShown) {
                                try {

                                    windowManager.removeView(floatingView[intent.getIntExtra("startId", 0)])

                                } catch (e: Exception) {
                                    e.printStackTrace()

                                } finally {
                                    PublicVariable.floatingFoldersList.remove(folderName[intent.getIntExtra("startId", 0)])
                                    PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1
                                    PublicVariable.floatingFolderCounter = PublicVariable.floatingFolderCounter - 1

                                    floatingFoldersUtils.floatingFoldersCounterType(this@FloatingFoldersForBluetooth.javaClass.simpleName)

                                    if (PublicVariable.allFloatingCounter == 0) {
                                        if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                                            stopService(Intent(applicationContext, BindServices::class.java))
                                        }
                                    }

                                    if (floatingFoldersCounterType[this@FloatingFoldersForBluetooth.javaClass.getSimpleName()] == 0) {

                                        stopSelf()
                                    }
                                }
                            } else if (PublicVariable.allFloatingCounter == 0) {
                                if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                                    stopService(Intent(applicationContext, BindServices::class.java))
                                }
                            }
                        } else if (intent.action == "Sticky_Edge${applicationContext.packageName}") {

                            for (stickyCounter in 0 until floatingView.size) {

                                stickedToEdge[stickyCounter] = true

                                stickyEdgeParams.add(stickyCounter, functionsClassLegacy.moveToEdge(this@FloatingFoldersForBluetooth.folderName.get(stickyCounter), layoutParams[stickyCounter].height))

                                if (floatingView[stickyCounter].isShown
                                        && floatingView[stickyCounter] != null) {

                                    try {

                                        windowManager
                                                .updateViewLayout(floatingView[stickyCounter],
                                                        stickyEdgeParams[stickyCounter])

                                    } catch (e: WindowManager.BadTokenException) {
                                        e.printStackTrace()
                                    }
                                }
                            }

                        } else if (intent.action == "Sticky_Edge_No${applicationContext.packageName}") {

                            for (stickyCounter in 0 until floatingView.size) {

                                stickedToEdge[stickyCounter] = false

                                val sharedPreferencesPositionBroadcast = getSharedPreferences(this@FloatingFoldersForBluetooth.folderName[stickyCounter], Context.MODE_PRIVATE)
                                XY.xPosition = sharedPreferencesPositionBroadcast.getInt("X", XY.xInitial)
                                XY.yPosition = sharedPreferencesPositionBroadcast.getInt("Y", XY.yInitial)

                                if (floatingView.get(stickyCounter).isShown
                                        && floatingView[stickyCounter] != null) {

                                    try {

                                        windowManager
                                                .updateViewLayout(floatingView[stickyCounter],
                                                        functionsClassLegacy.backFromEdge(layoutParams[stickyCounter].height, XY.xPosition, XY.yPosition))

                                    } catch (e: WindowManager.BadTokenException) {
                                        e.printStackTrace()
                                    }
                                }
                            }

                        } else if (intent.action == "Notification_Dot${applicationContext.packageName}") {

                            intent.getStringExtra("NotificationPackage")?.let {

                                val folderNameNotification = mapContentFolderName[it]
                                val startIdNotification = mapFolderNameStartId[folderNameNotification]

                                if (startIdNotification != null) {

                                    if (floatingView[startIdNotification].isShown) {
                                        /*Add Dot*/
                                        val dotDrawable = if (functionsClassLegacy.customIconsEnable()) {
                                            loadCustomIcons.getDrawableIconForPackage(it, functionsClassLegacy.shapedAppIcon(it).mutate()).mutate()
                                        } else {
                                            functionsClassLegacy.shapedAppIcon(it).mutate()
                                        }

                                        notificationDotView[startIdNotification].setImageDrawable(dotDrawable)
                                        notificationDotView[startIdNotification].visibility = View.VISIBLE
                                        notificationDotView[startIdNotification].tag = it
                                    }
                                }
                            }

                        } else if (intent.action == "Notification_Dot_No${applicationContext.packageName}") {

                            intent.getStringExtra("NotificationPackage")?.let {

                                val folderNameNotification = mapContentFolderName[it]
                                val startIdNotification = mapFolderNameStartId[folderNameNotification]

                                if (startIdNotification != null) {

                                    if (floatingView[startIdNotification].isShown) {
                                        /*Remove Dot*/
                                        notificationDotView[startIdNotification].visibility = View.INVISIBLE
                                    }
                                }
                            }
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    registerReceiver(broadcastReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
                } else {
                    registerReceiver(broadcastReceiver, intentFilter)
                }
            }

            if (showNotificationDot) {

                sendBroadcast(Intent("Notification_Dot")
                        .putExtra("NotificationPackage", alreadyNotificationPackage))
            }
        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}