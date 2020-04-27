/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 7:13 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts.FloatingServices

import android.annotation.SuppressLint
import android.app.Service
import android.content.*
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Handler
import android.os.IBinder
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.Utils.OpenActions
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.databinding.FloatingShortcutsBinding
import net.geekstools.imageview.customshapes.ShapesImage
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToInt

class FloatingShortcutsForApplications : Service() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }


    private lateinit var openActions: OpenActions


    private lateinit var windowManager: WindowManager


    private val layoutParams: ArrayList<WindowManager.LayoutParams> = ArrayList<WindowManager.LayoutParams>()
    private val stickyEdgeParams: ArrayList<WindowManager.LayoutParams> = ArrayList<WindowManager.LayoutParams>()

    private var moveDetection: WindowManager.LayoutParams? = null

    private object XY {
        var xPosition = 0
        var yPosition: Int = 0

        var xInitial: Int = 19
        var yInitial: Int = 19

        var xMove: Int = 0
        var yMove: Int = 0
    }

    private val activityInformation: ArrayList<ActivityInfo> = ArrayList<ActivityInfo>()


    private val packageNames: ArrayList<String> = ArrayList<String>()
    private val classNames: ArrayList<String> = ArrayList<String>()

    private val appIcons: ArrayList<Drawable> = ArrayList<Drawable>()
    private val iconColors: ArrayList<Int> = ArrayList<Int>()


    private val openPermit: ArrayList<Boolean> = ArrayList<Boolean>()
    private val movePermit: ArrayList<Boolean> = ArrayList<Boolean>()
    private val removePermit: ArrayList<Boolean> = ArrayList<Boolean>()

    private val touchingDelay: ArrayList<Boolean> = ArrayList<Boolean>()

    private val stickedToEdge: ArrayList<Boolean> = ArrayList<Boolean>()


    private val shapedIcons: ArrayList<ShapesImage> = ArrayList<ShapesImage>()
    private val controlIcons: ArrayList<ShapesImage> = ArrayList<ShapesImage>()


    lateinit var delayRunnable: Runnable
    lateinit var getBackRunnable: Runnable
    lateinit var runnablePressHold: Runnable

    var delayHandler: Handler = Handler()
    var getBackHandler: Handler = Handler()
    var handlerPressHold: Handler = Handler()


    val mapPackageNameStartId: HashMap<String, Int> = HashMap<String, Int>()


    private val simpleOnGestureListener: ArrayList<GestureDetector.SimpleOnGestureListener> = ArrayList<GestureDetector.SimpleOnGestureListener>()
    private val gestureDetector: ArrayList<GestureDetector> = ArrayList<GestureDetector>()

    private val flingAnimationX: ArrayList<FlingAnimation> = ArrayList<FlingAnimation>()
    private val flingAnimationY: ArrayList<FlingAnimation> = ArrayList<FlingAnimation>()

    var flingPositionX: Float = 0f
    var flingPositionY: Float = 0f


    private val layoutInflater: LayoutInflater by lazy {
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    private val floatingShortcutsBinding: ArrayList<FloatingShortcutsBinding> = ArrayList<FloatingShortcutsBinding>()

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, serviceStartId: Int): Int {
        FunctionsClassDebug.PrintDebug(this@FloatingShortcutsForApplications.javaClass.simpleName + " ::: StartId ::: " + serviceStartId)

        val startId = (serviceStartId - 1)

        intent?.run {

            if (this@run.hasExtra(getString(R.string.remove_all_floatings))) {
                if (this@run.getStringExtra(getString(R.string.remove_all_floatings)) == getString(R.string.remove_all_floatings)) {

                    for (removeCount in 0 until floatingShortcutsBinding.size) {
                        try {
                            if (floatingShortcutsBinding[removeCount].root.isShown) {
                                try {
                                    windowManager.removeView(floatingShortcutsBinding[removeCount].root)
                                } catch (e: WindowManager.InvalidDisplayException) {
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
                                if (!PreferenceManager.getDefaultSharedPreferences(applicationContext)
                                                .getBoolean("stable", true)) {
                                    stopService(Intent(applicationContext, BindServices::class.java))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    PublicVariable.floatingShortcutsList.clear()
                    PublicVariable.floatingShortcutsCounter = -1

                    stopSelf()
                }

                return START_NOT_STICKY
            }

            packageNames.add(startId, this@run.getStringExtra("PackageName"))

            if (!functionsClass.appIsInstalled(packageNames[startId])) {

                return START_NOT_STICKY
            }

            classNames.add(startId, this@run.getStringExtra("ClassName"))
            activityInformation.add(startId, packageManager.getActivityInfo(ComponentName(packageNames[startId], classNames[startId]), 0))

            floatingShortcutsBinding.add(startId, FloatingShortcutsBinding.inflate(layoutInflater))

            controlIcons.add(startId, functionsClass.initShapesImage(floatingShortcutsBinding[startId].controlIcon))
            shapedIcons.add(startId, functionsClass.initShapesImage(floatingShortcutsBinding[startId].shapedIcon))

            openPermit.add(startId, true)
            movePermit.add(startId, true)
            removePermit.add(startId, false)

            touchingDelay.add(startId, false)
            stickedToEdge.add(startId, false)

            mapPackageNameStartId.put(packageNames[startId], startId)

            /*Update Floating Shortcuts Database*/
            functionsClass.saveUnlimitedShortcutsService(packageNames[startId])
            functionsClass.updateRecoverShortcuts()
            /*Update Floating Shortcuts Database*/

            appIcons.add(startId, functionsClass.shapedAppIcon(activityInformation[startId]))
            iconColors.add(startId, functionsClass.extractDominantColor(functionsClass.appIcon(activityInformation[startId])))

            shapedIcons[startId].setImageDrawable(functionsClass.shapedAppIcon(activityInformation[startId]))

            val sharedPreferencesPosition = getSharedPreferences(classNames[startId], Context.MODE_PRIVATE)

            XY.xInitial = XY.xInitial + 13
            XY.yInitial = XY.yInitial + 13
            XY.xPosition = sharedPreferencesPosition.getInt("X", XY.xInitial)
            XY.yPosition = sharedPreferencesPosition.getInt("Y", XY.yInitial)
            layoutParams.add(startId, functionsClass.normalLayoutParams(PublicVariable.floatingViewsHW, XY.xPosition, XY.yPosition))

            try {
                floatingShortcutsBinding.get(startId).root.setTag(startId)
                windowManager.addView(floatingShortcutsBinding.get(startId).root, layoutParams[startId])
            } catch (e: WindowManager.BadTokenException) {
                e.printStackTrace()
            }

            XY.xMove = XY.xPosition
            XY.yMove = XY.yPosition

            if (PublicVariable.transparencyEnabled) {
                shapedIcons[startId].imageAlpha = PublicVariable.alpha
            }

            flingAnimationX.add(startId, FlingAnimation(FloatValueHolder())
                    .setFriction(3.0f)
                    .setMaxValue(functionsClass.displayX() - PublicVariable.floatingViewsHW.toFloat())
                    .setMinValue(0f))

            flingAnimationY.add(startId, FlingAnimation(FloatValueHolder())
                    .setFriction(3.0f)
                    .setMaxValue(functionsClass.displayY() - PublicVariable.floatingViewsHW.toFloat())
                    .setMinValue(0f))

            simpleOnGestureListener.add(startId, object : SimpleOnGestureListener() {

                override fun onFling(motionEventFirst: MotionEvent, motionEventLast: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

                    if (movePermit[startId]) {
                        flingAnimationX[startId].setStartVelocity(velocityX)
                        flingAnimationY[startId].setStartVelocity(velocityY)

                        flingAnimationX[startId].setStartValue(flingPositionX)
                        flingAnimationY[startId].setStartValue(flingPositionY)

                        flingAnimationX[startId].addUpdateListener { animation, value, velocity ->

                            if (floatingShortcutsBinding.get(startId).root.isShown) {
                                layoutParams[startId].x = value.toInt() // X movePoint

                                windowManager.updateViewLayout(floatingShortcutsBinding.get(startId).root, layoutParams[startId])

                                XY.xMove = value.toInt()
                            }
                        }

                        flingAnimationY[startId].addUpdateListener { animation, value, velocity ->

                            if (floatingShortcutsBinding.get(startId).root.isShown) {
                                layoutParams[startId].y = value.toInt() // Y movePoint

                                windowManager.updateViewLayout(floatingShortcutsBinding.get(startId).root, layoutParams[startId])

                                XY.yMove = value.toInt()
                            }
                        }
                        flingAnimationX[startId].start()
                        flingAnimationY[startId].start()

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

            floatingShortcutsBinding[startId].root.setOnTouchListener(object : OnTouchListener {

                var initialX = 0
                var initialY = 0

                var initialTouchX = 0f
                var initialTouchY = 0f

                @SuppressLint("ClickableViewAccessibility")
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

                            XY.xMove = (initialTouchX).roundToInt()
                            XY.yMove = round(initialTouchY).roundToInt()

                            touchingDelay[startId] = true
                            delayRunnable = Runnable {
                                if (touchingDelay[startId]) {
                                    removePermit[startId] = true
                                    
                                    val drawClose = getDrawable(R.drawable.draw_close_service) as LayerDrawable?
                                    val backgroundTemporary = drawClose!!.findDrawableByLayerId(R.id.backgroundTemporary)
                                    backgroundTemporary.setTint(iconColors[startId])
                                    controlIcons[startId].setImageDrawable(drawClose)
                                    
                                    functionsClass.doVibrate(100)
                                    
                                    sendBroadcast(Intent("Hide_PopupListView_Shortcuts"))
                                    
                                    getBackRunnable = Runnable {
                                        if (removePermit[startId]) {
                                            removePermit[startId] = false
                                            controlIcons[startId].setImageDrawable(null)
                                        }
                                    }
                                    getBackHandler.postDelayed(getBackRunnable, 3333 + 333.toLong())
                                }
                            }
                            delayHandler.postDelayed(delayRunnable, 3333 + 333.toLong())

                            runnablePressHold = Runnable {
                                if (touchingDelay[startId]) {

                                    functionsClass.PopupShortcuts(
                                            floatingShortcutsBinding.get(startId).root,
                                            packageNames.get(startId),
                                            this@FloatingShortcutsForApplications.javaClass.simpleName,
                                            startId,
                                            initialX,
                                            initialY
                                    )

                                    openPermit[startId] = false
                                }
                            }

                            handlerPressHold.postDelayed(runnablePressHold, 333.toLong())
                        }
                        MotionEvent.ACTION_UP -> {
                            touchingDelay[startId] = false

                            delayHandler.removeCallbacks(delayRunnable)
                            handlerPressHold.removeCallbacks(runnablePressHold)

                            Handler().postDelayed({
                                openPermit[startId] = true
                            }, 130)

                            if (movePermit[startId]) {
                                layoutParamsOnTouch.x = initialX + (motionEvent.rawX - initialTouchX).toInt()
                                layoutParamsOnTouch.y = initialY + (motionEvent.rawY - initialTouchY).toInt()

                                XY.xMove = (layoutParamsOnTouch.x)
                                XY.yMove = (layoutParamsOnTouch.y)

                                getSharedPreferences(classNames[startId], Context.MODE_PRIVATE).edit().apply {
                                    putInt("X", layoutParamsOnTouch.x)
                                    putInt("Y", layoutParamsOnTouch.y)

                                    apply()
                                }
                            } else {
                                var initialTouchXBoundBack = getSharedPreferences(classNames[startId], Context.MODE_PRIVATE).getInt("X", 0).toFloat()

                                if (initialTouchXBoundBack < 0) {
                                    initialTouchXBoundBack = 0f
                                } else if (initialTouchXBoundBack > functionsClass.displayX()) {
                                    initialTouchXBoundBack = functionsClass.displayX().toFloat()
                                }

                                var initialTouchYBoundBack = getSharedPreferences(classNames[startId], Context.MODE_PRIVATE).getInt("Y", 0).toFloat()

                                if (initialTouchYBoundBack < 0) {
                                    initialTouchYBoundBack = 0f
                                } else if (initialTouchYBoundBack > functionsClass.displayY()) {
                                    initialTouchYBoundBack = functionsClass.displayY().toFloat()
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
                                } else if (springStartValueX > functionsClass.displayX()) {
                                    springStartValueX = functionsClass.displayX().toFloat()
                                }

                                var springStartValueY = motionEvent.rawY
                                if (springStartValueY < 0f) {
                                    springStartValueY = 0f
                                } else if (springStartValueY > functionsClass.displayY()) {
                                    springStartValueY = functionsClass.displayY().toFloat()
                                }

                                springAnimationX.setStartValue(springStartValueX)
                                springAnimationX.setStartVelocity(-0f)
                                springAnimationX.setMaxValue(functionsClass.displayX().toFloat())

                                springAnimationY.setStartValue(springStartValueY)
                                springAnimationY.setStartVelocity(-0f)
                                springAnimationY.setMaxValue(functionsClass.displayY().toFloat())

                                springAnimationX.addUpdateListener { animation, value, velocity ->

                                    if (floatingShortcutsBinding.get(startId).root.isShown()) {
                                        layoutParamsOnTouch.x = value.toInt() // X movePoint
                                        windowManager.updateViewLayout(floatingShortcutsBinding.get(startId).root, layoutParamsOnTouch)
                                    }
                                }
                                springAnimationY.addUpdateListener { animation, value, velocity ->
                                    if (floatingShortcutsBinding.get(startId).root.isShown()) {
                                        layoutParamsOnTouch.y = value.toInt() // Y movePoint
                                        windowManager.updateViewLayout(floatingShortcutsBinding.get(startId).root, layoutParamsOnTouch)
                                    }
                                }
                                springAnimationX.addEndListener { animation, canceled, value, velocity ->

                                    openPermit[startId] = true
                                }
                                springAnimationY.addEndListener { animation, canceled, value, velocity ->

                                    openPermit[startId] = true
                                }

                                springAnimationX.start()
                                springAnimationY.start()

                            }
                            moveDetection = layoutParamsOnTouch
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (movePermit[startId]) {

                                layoutParamsOnTouch.x = initialX + (motionEvent.rawX - initialTouchX).toInt()
                                layoutParamsOnTouch.y = initialY + (motionEvent.rawY - initialTouchY).toInt()

                                windowManager.updateViewLayout(floatingShortcutsBinding[startId].root, layoutParamsOnTouch)

                                moveDetection = layoutParamsOnTouch

                                val difMoveX = (layoutParamsOnTouch.x - initialTouchX).toInt()
                                val difMoveY = (layoutParamsOnTouch.y - initialTouchY).toInt()

                                if (abs(difMoveX) > abs(PublicVariable.floatingViewsHW + PublicVariable.floatingViewsHW * 70 / 100)
                                        || abs(difMoveY) > abs(PublicVariable.floatingViewsHW + PublicVariable.floatingViewsHW * 70 / 100)) {

                                    sendBroadcast(Intent("Hide_PopupListView_Shortcuts"))

                                    openPermit[startId] = false
                                    touchingDelay[startId] = false

                                    delayHandler.removeCallbacks(delayRunnable)
                                    handlerPressHold.removeCallbacks(runnablePressHold)
                                }

                                flingPositionX = layoutParamsOnTouch.x.toFloat()
                                flingPositionY = layoutParamsOnTouch.y.toFloat()
                            } else {

                                layoutParamsOnTouch.x = initialX + (motionEvent.rawX - initialTouchX).toInt() // X movePoint
                                layoutParamsOnTouch.y = initialY + (motionEvent.rawY - initialTouchY).toInt() // Y movePoint

                                windowManager.updateViewLayout(floatingShortcutsBinding.get(startId).root, layoutParamsOnTouch)

                                val difMoveX = (layoutParamsOnTouch.x - initialTouchX).toInt()
                                val difMoveY = (layoutParamsOnTouch.y - initialTouchY).toInt()

                                if (abs(difMoveX) > abs(PublicVariable.floatingViewsHW + PublicVariable.floatingViewsHW * 70 / 100)
                                        || abs(difMoveY) > abs(PublicVariable.floatingViewsHW + PublicVariable.floatingViewsHW * 70 / 100)) {

                                    sendBroadcast(Intent("Hide_PopupListView_Shortcuts"))

                                    openPermit[startId] = false
                                    touchingDelay[startId] = false

                                    delayHandler.removeCallbacks(delayRunnable)
                                    handlerPressHold.removeCallbacks(runnablePressHold)
                                }

                            }
                        }
                    }

                    return false
                }
            })

            floatingShortcutsBinding[startId].root.setOnClickListener {
                if (removePermit[startId]) {

                    if (floatingShortcutsBinding[startId].root.isShown) {
                        try {

                            windowManager.removeView(floatingShortcutsBinding[startId].root)
                            getBackHandler.removeCallbacks(getBackRunnable)

                        } catch (e: Exception) {
                            e.printStackTrace()

                        } finally {
                            PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1

                            PublicVariable.floatingShortcutsList.remove(packageNames[startId])
                            PublicVariable.floatingShortcutsCounter = PublicVariable.floatingShortcutsCounter - 1

                            if (PublicVariable.allFloatingCounter == 0) {
                                if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                                    stopService(Intent(applicationContext, BindServices::class.java))
                                }

                                stopSelf()
                            }
                        }
                    }
                } else {
                    if (openPermit[startId]) {

                        openActions.startProcess(packageNames[startId], classNames[startId],
                                if (moveDetection != null) {
                                    (moveDetection!!)
                                    (moveDetection!!)
                                } else {
                                    (layoutParams[startId])
                                    (layoutParams[startId])
                                })

                    } else {

                    }
                }
            }

            if (serviceStartId == 1) {
                val floatingShortcutClassInCommand: String = this@FloatingShortcutsForApplications.javaClass.simpleName

                val intentFilter = IntentFilter()
                intentFilter.addAction("Pin_App_$floatingShortcutClassInCommand")
                intentFilter.addAction("Unpin_App_$floatingShortcutClassInCommand")
                intentFilter.addAction("Remove_App_$floatingShortcutClassInCommand")
                val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

                    override fun onReceive(context: Context, intent: Intent) {

                        if (intent.action == "Pin_App_$floatingShortcutClassInCommand") {

                            movePermit[intent.getIntExtra("startId", 1)] = false
                            var pinDrawable: Drawable = functionsClass.appIcon(activityInformation[intent.getIntExtra("startId", 1)]).mutate()

                            when (functionsClass.shapesImageId()) {
                                1 -> {
                                    pinDrawable = getDrawable(R.drawable.pin_droplet_icon) as Drawable
                                    controlIcons[intent.getIntExtra("startId", 1)].setPadding(-3, -3, -3, -3)
                                }
                                2 -> {
                                    pinDrawable = getDrawable(R.drawable.pin_circle_icon) as Drawable
                                }
                                3 -> {
                                    pinDrawable = getDrawable(R.drawable.pin_square_icon) as Drawable
                                }
                                4 -> {
                                    pinDrawable = getDrawable(R.drawable.pin_squircle_icon) as Drawable
                                }
                                0 -> {
                                    pinDrawable = functionsClass.appIcon(activityInformation[intent.getIntExtra("startId", 1)]).mutate()
                                }
                            }

                            pinDrawable.setTint(functionsClass.setColorAlpha(Color.RED, 175f))

                            if (functionsClass.returnAPI() >= 26) {
                                pinDrawable.alpha = 175
                                pinDrawable.setTint(Color.RED)

                                controlIcons[intent.getIntExtra("startId", 1)].alpha = 0.50f
                            }

                            controlIcons[intent.getIntExtra("startId", 1)].setImageDrawable(pinDrawable)

                        } else if (intent.action == "Unpin_App_$floatingShortcutClassInCommand") {

                            movePermit[intent.getIntExtra("startId", 1)] = true
                            controlIcons[intent.getIntExtra("startId", 1)].setImageDrawable(null)

                        } else if (intent.action == "Remove_App_$floatingShortcutClassInCommand") {

                            if (floatingShortcutsBinding.get(intent.getIntExtra("startId", 0)) == null) {

                                return
                            }

                            if (floatingShortcutsBinding.get(intent.getIntExtra("startId", 0)).root.isShown()) {
                                try {
                                    windowManager.removeView(floatingShortcutsBinding.get(intent.getIntExtra("startId", 0)).root)
                                } catch (e: Exception) {
                                    e.printStackTrace()

                                } finally {
                                    PublicVariable.floatingShortcutsList.remove(packageNames.get(intent.getIntExtra("startId", 0)))
                                    PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1
                                    PublicVariable.floatingShortcutsCounter = PublicVariable.floatingShortcutsCounter - 1

                                    if (PublicVariable.allFloatingCounter == 0) {
                                        stopService(Intent(applicationContext, BindServices::class.java))

                                        stopSelf()
                                    }
                                }
                            }

                        }
                    }
                }
                registerReceiver(broadcastReceiver, intentFilter)
            }
        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        openActions = OpenActions(applicationContext, functionsClass)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}