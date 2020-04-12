package net.geekstools.floatshort.PRO.Shortcuts.FloatingServices

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.Utils.OpenActions
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.FloatingShortcutsBinding
import net.geekstools.imageview.customshapes.ShapesImage
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToInt

class FloatingShortcutsForFrequentlyApplications : Service() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private lateinit var securityFunctions: SecurityFunctions


    private lateinit var openActions: OpenActions


    private lateinit var windowManager: WindowManager


    private val layoutParams: ArrayList<WindowManager.LayoutParams> = ArrayList<WindowManager.LayoutParams>()
    private val stickyEdgeParams: ArrayList<WindowManager.LayoutParams> = ArrayList<WindowManager.LayoutParams>()

    private var moveDetection: WindowManager.LayoutParams? = null

    private object XY {
        var xPosition = 0
        var yPosition: Int = 0

        var xInitial: Int = 13
        var yInitial: Int = 13

        var xMove: Int = 0
        var yMove: Int = 0
    }

    private val packageNames: ArrayList<String> = ArrayList<String>()

    private val appIcons: ArrayList<Drawable> = ArrayList<Drawable>()
    private val iconColors: ArrayList<Int> = ArrayList<Int>()


    private val openPermit: ArrayList<Boolean> = ArrayList<Boolean>()
    private val movePermit: ArrayList<Boolean> = ArrayList<Boolean>()
    private val removePermit: ArrayList<Boolean> = ArrayList<Boolean>()

    private val touchingDelay: ArrayList<Boolean> = ArrayList<Boolean>()

    private val stickedToEdge: ArrayList<Boolean> = ArrayList<Boolean>()


    private val shapedIcons: ArrayList<ShapesImage> = ArrayList<ShapesImage>()
    private val controlIcons: ArrayList<ShapesImage> = ArrayList<ShapesImage>()
    private val notificationDots: ArrayList<ShapesImage> = ArrayList<ShapesImage>()


    private object AuthenticationProcess {
        var authenticationProcessInvoked: Boolean = false
        var authenticationProcessInvokedName: String = ""
    }

    lateinit var delayRunnable: Runnable
    lateinit var getBackRunnable: Runnable
    lateinit var runnablePressHold: Runnable

    var delayHandler = Handler()
    var getBackHandler: Handler = Handler()
    var handlerPressHold: Handler = Handler()


    val mapPackageNameStartId: HashMap<String, Int> = HashMap<String, Int>()


    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {

                for (J in 0 until floatingShortcutsBinding.size) {
                    try {
                        if (floatingShortcutsBinding[J].root.isShown) {
                            layoutParams.set(J,
                                    functionsClass.handleOrientationPortrait(packageNames[J], layoutParams[J].height))

                            windowManager.updateViewLayout(floatingShortcutsBinding[J].root, layoutParams[J])
                        }
                    } catch (e: WindowManager.InvalidDisplayException) {
                        e.printStackTrace()
                    }
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {

                for (J in 0 until floatingShortcutsBinding.size) {
                    try {
                        if (floatingShortcutsBinding[J].root.isShown) {
                            layoutParams.set(J,
                                    functionsClass.handleOrientationLandscape(packageNames[J], layoutParams[J].height))

                            windowManager.updateViewLayout(floatingShortcutsBinding[J].root, layoutParams[J])
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
        FunctionsClassDebug.PrintDebug(this@FloatingShortcutsForFrequentlyApplications.javaClass.simpleName + " ::: StartId ::: " + serviceStartId)

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
                                if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                                    stopService(Intent(applicationContext, BindServices::class.java))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    stopSelf()
                }

                return Service.START_NOT_STICKY
            }

            packageNames.add(startId, this@run.getStringExtra("PackageName"))

            if (!functionsClass.appIsInstalled(packageNames[startId])) {

                return Service.START_NOT_STICKY
            }

            floatingShortcutsBinding.add(startId, FloatingShortcutsBinding.inflate(layoutInflater))

            controlIcons.add(startId, functionsClass.initShapesImage(floatingShortcutsBinding[startId].controlIcon))
            shapedIcons.add(startId, functionsClass.initShapesImage(floatingShortcutsBinding[startId].shapedIcon))
            notificationDots.add(startId, functionsClass.initShapesImage(if (functionsClass.checkStickyEdge()) {
                floatingShortcutsBinding[startId].notificationDotEnd
            } else {
                floatingShortcutsBinding[startId].notificationDotStart
            }))

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

            appIcons.add(startId, functionsClass.shapedAppIcon(packageNames[startId]))
            iconColors.add(startId, functionsClass.extractDominantColor(functionsClass.appIcon(packageNames[startId])))

            shapedIcons[startId].setImageDrawable(if (functionsClass.customIconsEnable()) {
                loadCustomIcons.getDrawableIconForPackage(packageNames[startId], functionsClass.shapedAppIcon(packageNames[startId]))
            } else {
                functionsClass.shapedAppIcon(packageNames[startId])
            })

            val sharedPreferencesPosition = getSharedPreferences(packageNames[startId], Context.MODE_PRIVATE)

            PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39)
            PublicVariable.HW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size.toFloat(), applicationContext.resources.displayMetrics).toInt()

            XY.xInitial = XY.xInitial + 13
            XY.yInitial = XY.yInitial + 13
            XY.xPosition = sharedPreferencesPosition.getInt("X", XY.xInitial)
            XY.yPosition = sharedPreferencesPosition.getInt("Y", XY.yInitial)
            layoutParams.add(startId, functionsClass.normalLayoutParams(PublicVariable.HW, XY.xPosition, XY.yPosition))

            try {
                floatingShortcutsBinding.get(startId).root.setTag(startId)
                windowManager.addView(floatingShortcutsBinding.get(startId).root, layoutParams[startId])
            } catch (e: WindowManager.BadTokenException) {
                e.printStackTrace()
            }

            XY.xMove = XY.xPosition
            XY.yMove = XY.yPosition

            shapedIcons[startId].imageAlpha = functionsClass.readDefaultPreference("autoTrans", 255)

            if (!functionsClass.litePreferencesEnabled()) {

                flingAnimationX.add(startId, FlingAnimation(FloatValueHolder())
                        .setFriction(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f))
                        .setMaxValue(functionsClass.displayX() - PublicVariable.HW.toFloat())
                        .setMinValue(0f))

                flingAnimationY.add(startId, FlingAnimation(FloatValueHolder())
                        .setFriction(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f))
                        .setMaxValue(functionsClass.displayY() - PublicVariable.HW.toFloat())
                        .setMinValue(0f))

                simpleOnGestureListener.add(startId, object : GestureDetector.SimpleOnGestureListener() {

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
            }

            floatingShortcutsBinding[startId].root.setOnTouchListener(object : View.OnTouchListener {

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
                                    getBackHandler.postDelayed(getBackRunnable, 3333 + functionsClass.readDefaultPreference("delayPressHold", 333).toLong())
                                }
                            }
                            delayHandler.postDelayed(delayRunnable, 3333 + functionsClass.readDefaultPreference("delayPressHold", 333).toLong())

                            runnablePressHold = Runnable {
                                if (touchingDelay[startId]) {
                                    functionsClass.PopupOptionShortcuts(
                                            floatingShortcutsBinding.get(startId).root,
                                            packageNames[startId],
                                            this@FloatingShortcutsForFrequentlyApplications.javaClass.simpleName,
                                            startId,
                                            initialX,
                                            initialY
                                    )
                                    openPermit[startId] = false
                                }
                            }

                            handlerPressHold.postDelayed(runnablePressHold, functionsClass.readDefaultPreference("delayPressHold", 333).toLong())
                        }
                        MotionEvent.ACTION_UP -> {
                            touchingDelay[startId] = false

                            delayHandler.removeCallbacks(delayRunnable)
                            handlerPressHold.removeCallbacks(runnablePressHold)

                            Handler().postDelayed({
                                openPermit[startId] = true
                            }, 113)

                            if (movePermit[startId]) {
                                layoutParamsOnTouch.x = initialX + (motionEvent.rawX - initialTouchX).toInt()
                                layoutParamsOnTouch.y = initialY + (motionEvent.rawY - initialTouchY).toInt()

                                XY.xMove = (layoutParamsOnTouch.x)
                                XY.yMove = (layoutParamsOnTouch.y)

                                getSharedPreferences(packageNames[startId], Context.MODE_PRIVATE).edit().apply {
                                    putInt("X", layoutParamsOnTouch.x)
                                    putInt("Y", layoutParamsOnTouch.y)

                                    apply()
                                }
                            } else {
                                if (!functionsClass.litePreferencesEnabled()) {
                                    var initialTouchXBoundBack = getSharedPreferences(packageNames[startId], Context.MODE_PRIVATE).getInt("X", 0).toFloat()

                                    if (initialTouchXBoundBack < 0) {
                                        initialTouchXBoundBack = 0f
                                    } else if (initialTouchXBoundBack > functionsClass.displayX()) {
                                        initialTouchXBoundBack = functionsClass.displayX().toFloat()
                                    }

                                    var initialTouchYBoundBack = getSharedPreferences(packageNames[startId], Context.MODE_PRIVATE).getInt("Y", 0).toFloat()

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
                            }
                            moveDetection = layoutParamsOnTouch
                        }
                        MotionEvent.ACTION_MOVE -> if (movePermit[startId]) {

                            layoutParamsOnTouch.x = initialX + (motionEvent.rawX - initialTouchX).toInt()
                            layoutParamsOnTouch.y = initialY + (motionEvent.rawY - initialTouchY).toInt()

                            windowManager.updateViewLayout(floatingShortcutsBinding[startId].root, layoutParamsOnTouch)

                            moveDetection = layoutParamsOnTouch

                            val difMoveX = (layoutParamsOnTouch.x - initialTouchX).toInt()
                            val difMoveY = (layoutParamsOnTouch.y - initialTouchY).toInt()

                            if (abs(difMoveX) > abs(PublicVariable.HW + PublicVariable.HW * 70 / 100)
                                    || abs(difMoveY) > abs(PublicVariable.HW + PublicVariable.HW * 70 / 100)) {

                                sendBroadcast(Intent("Hide_PopupListView_Shortcuts"))

                                openPermit[startId] = false
                                touchingDelay[startId] = false

                                delayHandler.removeCallbacks(delayRunnable)
                                handlerPressHold.removeCallbacks(runnablePressHold)
                            }

                            flingPositionX = layoutParamsOnTouch.x.toFloat()
                            flingPositionY = layoutParamsOnTouch.y.toFloat()
                        } else {

                            if (!functionsClass.litePreferencesEnabled()) {

                                layoutParamsOnTouch.x = initialX + (motionEvent.rawX - initialTouchX).toInt() // X movePoint
                                layoutParamsOnTouch.y = initialY + (motionEvent.rawY - initialTouchY).toInt() // Y movePoint

                                windowManager.updateViewLayout(floatingShortcutsBinding.get(startId).root, layoutParamsOnTouch)

                                val difMoveX = (layoutParamsOnTouch.x - initialTouchX).toInt()
                                val difMoveY = (layoutParamsOnTouch.y - initialTouchY).toInt()

                                if (abs(difMoveX) > abs(PublicVariable.HW + PublicVariable.HW * 70 / 100)
                                        || abs(difMoveY) > abs(PublicVariable.HW + PublicVariable.HW * 70 / 100)) {

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

                        if (securityFunctions.isAppLocked(packageNames[startId])) {

                            if (!AuthenticationProcess.authenticationProcessInvoked) {

                                AuthenticationProcess.authenticationProcessInvoked = true
                                AuthenticationProcess.authenticationProcessInvokedName = functionsClass.appName(packageNames[startId])

                                SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                                    override fun authenticatedFloatIt(extraInformation: Bundle?) {
                                        super.authenticatedFloatIt(extraInformation)
                                        Log.d(this@FloatingShortcutsForFrequentlyApplications.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                                        openActions.startProcess(packageNames[startId],
                                                if (moveDetection != null) {
                                                    (moveDetection!!)
                                                    (moveDetection!!)
                                                } else {
                                                    (layoutParams[startId])
                                                    (layoutParams[startId])
                                                })

                                        AuthenticationProcess.authenticationProcessInvoked = false
                                    }

                                    override fun failedAuthenticated() {
                                        super.failedAuthenticated()
                                        Log.d(this@FloatingShortcutsForFrequentlyApplications.javaClass.simpleName, "FailedAuthenticated")

                                        AuthenticationProcess.authenticationProcessInvoked = false
                                    }

                                    override fun invokedPinPassword() {
                                        super.invokedPinPassword()
                                        Log.d(this@FloatingShortcutsForFrequentlyApplications.javaClass.simpleName, "InvokedPinPassword")

                                        AuthenticationProcess.authenticationProcessInvoked = false
                                    }
                                }

                                startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
                                    putExtra("OtherTitle", functionsClass.appName(packageNames[startId]))
                                    putExtra("PrimaryColor", iconColors[startId])
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())

                            } else {

                                Toast.makeText(applicationContext,
                                        Html.fromHtml(getString(R.string.authenticationProcessInvoked, AuthenticationProcess.authenticationProcessInvokedName)),
                                        Toast.LENGTH_LONG)
                                        .show()
                            }
                        } else {

                            openActions.startProcess(packageNames[startId],
                                    if (moveDetection != null) {
                                        (moveDetection!!)
                                        (moveDetection!!)
                                    } else {
                                        (layoutParams[startId])
                                        (layoutParams[startId])
                                    })
                        }
                    } else {

                    }
                }
            }

            notificationDots[startId].setOnClickListener {

                functionsClass.PopupNotificationShortcuts(
                        floatingShortcutsBinding[startId].root,
                        packageNames[startId],
                        this@FloatingShortcutsForFrequentlyApplications.javaClass.simpleName,
                        startId,
                        iconColors[startId],
                        XY.xMove,
                        XY.yMove,
                        layoutParams[startId].width
                )
            }

            notificationDots[startId].setOnLongClickListener { view ->

                if (functionsClass.AccessibilityServiceEnabled() && functionsClass.SettingServiceRunning(InteractionObserver::class.java)) {

                    functionsClass.sendInteractionObserverEvent(view,
                            packageNames[startId],
                            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                            66666)
                } else {
                    try {
                        @SuppressLint("WrongConstant")
                        val statusBarService = getSystemService("statusbar")
                        val statusBarManager = Class.forName("android.app.StatusBarManager")
                        val statuesBarInvocation = statusBarManager.getMethod("expandNotificationsPanel") //expandNotificationsPanel
                        statuesBarInvocation.invoke(statusBarService)
                    } catch (firstException: Exception) {
                        firstException.printStackTrace()

                        try {
                            @SuppressLint("WrongConstant")
                            val statusBarService = getSystemService("statusbar")
                            val statusBarManager = Class.forName("android.app.StatusBarManager")
                            val statuesBarInvocation = statusBarManager.getMethod("expand") //expandNotificationsPanel
                            statuesBarInvocation.invoke(statusBarService)
                        } catch (secondException: Exception) {
                            secondException.printStackTrace()
                        }
                    }
                }
                true
            }

            if (serviceStartId == 1) {
                val floatingShortcutClassInCommand: String = this@FloatingShortcutsForFrequentlyApplications.javaClass.simpleName

                val intentFilter = IntentFilter()
                intentFilter.addAction("Split_Apps_Single_$floatingShortcutClassInCommand")
                intentFilter.addAction("Pin_App_$floatingShortcutClassInCommand")
                intentFilter.addAction("Unpin_App_$floatingShortcutClassInCommand")
                intentFilter.addAction("Float_It_$floatingShortcutClassInCommand")
                intentFilter.addAction("Remove_App_$floatingShortcutClassInCommand")
                intentFilter.addAction("Sticky_Edge")
                intentFilter.addAction("Sticky_Edge_No")
                intentFilter.addAction("Notification_Dot")
                intentFilter.addAction("Notification_Dot_No")
                val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

                    override fun onReceive(context: Context, intent: Intent) {
                        if (intent.action == "Split_Apps_Single_$floatingShortcutClassInCommand" && PublicVariable.splitScreen) {
                            FunctionsClassDebug.PrintDebug("Split Apps Single")

                            PublicVariable.splitScreen = false
                            Handler().postDelayed({
                                try {
                                    var splitSingle: Intent? = Intent()
                                    if (PublicVariable.splitSingleClassName != null) {
                                        splitSingle!!.setClassName(PublicVariable.splitSinglePackage, PublicVariable.splitSingleClassName)
                                    } else {
                                        splitSingle = packageManager.getLaunchIntentForPackage(PublicVariable.splitSinglePackage)
                                    }
                                    splitSingle!!.flags = Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                                    startActivity(splitSingle)

                                    PublicVariable.splitScreen = true

                                    functionsClass.Toast(functionsClass.appName(PublicVariable.splitSinglePackage), Gravity.TOP)

                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                }
                            }, 200)
                        } else if (intent.action == "Pin_App_$floatingShortcutClassInCommand") {
                            FunctionsClassDebug.PrintDebug(functionsClass.appName(packageNames[intent.getIntExtra("startId", 1)]))

                            movePermit[intent.getIntExtra("startId", 1)] = false
                            var pinDrawable: Drawable = functionsClass.appIcon(packageNames[intent.getIntExtra("startId", 1)]).mutate()

                            if (functionsClass.customIconsEnable()) {
                                pinDrawable = functionsClass.getAppIconDrawableCustomIcon(packageNames[intent.getIntExtra("startId", 1)]).mutate()
                            } else {
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
                                        pinDrawable = functionsClass.appIcon(packageNames[intent.getIntExtra("startId", 1)]).mutate()
                                    }
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
                            FunctionsClassDebug.PrintDebug(functionsClass.appName(packageNames[intent.getIntExtra("startId", 1)]))

                            movePermit[intent.getIntExtra("startId", 1)] = true
                            controlIcons[intent.getIntExtra("startId", 1)].setImageDrawable(null)
                        } else if (intent.action == "Float_It_$floatingShortcutClassInCommand") {
                            if (securityFunctions.isAppLocked(packageNames[intent.getIntExtra("startId", 1)])) {

                                if (!AuthenticationProcess.authenticationProcessInvoked) {

                                    AuthenticationProcess.authenticationProcessInvoked = true
                                    AuthenticationProcess.authenticationProcessInvokedName = functionsClass.appName(packageNames[intent.getIntExtra("startId", 1)])

                                    SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                                        override fun authenticatedFloatIt(extraInformation: Bundle?) {
                                            super.authenticatedFloatIt(extraInformation)
                                            Log.d(this@FloatingShortcutsForFrequentlyApplications.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                                            openActions.startProcess(packageNames[intent.getIntExtra("startId", 1)],
                                                    if (moveDetection != null) {
                                                        (moveDetection!!)
                                                        (moveDetection!!)
                                                    } else {
                                                        (layoutParams[intent.getIntExtra("startId", 1)])
                                                        (layoutParams[intent.getIntExtra("startId", 1)])
                                                    })

                                            AuthenticationProcess.authenticationProcessInvoked = false
                                        }

                                        override fun failedAuthenticated() {
                                            super.failedAuthenticated()
                                            Log.d(this@FloatingShortcutsForFrequentlyApplications.javaClass.simpleName, "FailedAuthenticated")

                                            AuthenticationProcess.authenticationProcessInvoked = false
                                        }

                                        override fun invokedPinPassword() {
                                            super.invokedPinPassword()
                                            Log.d(this@FloatingShortcutsForFrequentlyApplications.javaClass.simpleName, "InvokedPinPassword")

                                            AuthenticationProcess.authenticationProcessInvoked = false
                                        }
                                    }

                                    startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
                                        putExtra("OtherTitle", functionsClass.appName(packageNames[intent.getIntExtra("startId", 1)]))
                                        putExtra("PrimaryColor", iconColors[intent.getIntExtra("startId", 1)])
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())

                                } else {

                                    Toast.makeText(applicationContext,
                                            Html.fromHtml(getString(R.string.authenticationProcessInvoked, AuthenticationProcess.authenticationProcessInvokedName)),
                                            Toast.LENGTH_LONG)
                                            .show()
                                }
                            } else {

                                openActions.startProcess(packageNames[intent.getIntExtra("startId", 1)],
                                        if (moveDetection != null) {
                                            (moveDetection!!)
                                            (moveDetection!!)
                                        } else {
                                            (layoutParams[intent.getIntExtra("startId", 1)])
                                            (layoutParams[intent.getIntExtra("startId", 1)])
                                        })
                            }
                        } else if (intent.action == "Remove_App_$floatingShortcutClassInCommand") {
                            if (floatingShortcutsBinding.get(intent.getIntExtra("startId", 1)) == null) {
                                return
                            }

                            if (floatingShortcutsBinding.get(intent.getIntExtra("startId", 1)).root.isShown()) {
                                try {
                                    windowManager.removeView(floatingShortcutsBinding.get(intent.getIntExtra("startId", 1)).root)
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                } finally {
                                    PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1

                                    if (PublicVariable.allFloatingCounter == 0) {
                                        if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                                            stopService(Intent(applicationContext, BindServices::class.java))
                                        }

                                        stopSelf()
                                    }
                                }
                            }
                        } else if (intent.action == "Sticky_Edge") {

                            for (stickyCounter in 0 until floatingShortcutsBinding.size) {
                                try {
                                    if (floatingShortcutsBinding[stickyCounter].root.isShown) {
                                        try {
                                            stickedToEdge[stickyCounter] = true

                                            stickyEdgeParams.add(stickyCounter, functionsClass.moveToEdge(this@FloatingShortcutsForFrequentlyApplications.packageNames.get(stickyCounter), layoutParams[stickyCounter].height))

                                            windowManager.updateViewLayout(floatingShortcutsBinding[stickyCounter].root, stickyEdgeParams[stickyCounter])

                                        } catch (e: WindowManager.InvalidDisplayException) {
                                            e.printStackTrace()
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else if (intent.action == "Sticky_Edge_No") {
                            for (stickyCounter in 0 until floatingShortcutsBinding.size) {
                                try {
                                    if (floatingShortcutsBinding[stickyCounter].root.isShown) {
                                        try {
                                            try {
                                                stickedToEdge[stickyCounter] = false

                                                val sharedPreferencesPositionBroadcast = getSharedPreferences(this@FloatingShortcutsForFrequentlyApplications.packageNames.get(stickyCounter), Context.MODE_PRIVATE)
                                                XY.xPosition = sharedPreferencesPositionBroadcast.getInt("X", XY.xInitial)
                                                XY.yPosition = sharedPreferencesPositionBroadcast.getInt("Y", XY.yInitial)

                                                windowManager.updateViewLayout(floatingShortcutsBinding.get(stickyCounter).root, functionsClass.backFromEdge(layoutParams[stickyCounter].height, XY.xPosition, XY.yPosition))
                                            } catch (e: WindowManager.BadTokenException) {
                                                e.printStackTrace()
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else if (intent.action == "Notification_Dot") {
                            try {
                                val notificationPackage: String = intent.getStringExtra("NotificationPackage")
                                val startIdNotification: Int = mapPackageNameStartId[notificationPackage]!!

                                if (floatingShortcutsBinding.get(startIdNotification) != null) {
                                    if (floatingShortcutsBinding.get(startIdNotification).root.isShown) {
                                        /*add dot*/
                                        var dotDrawable: Drawable = functionsClass.appIcon(packageNames[startIdNotification]).mutate()
                                        if (functionsClass.customIconsEnable()) {
                                            dotDrawable = functionsClass.getAppIconDrawableCustomIcon(packageNames[startIdNotification]).mutate()
                                        } else {
                                            when (functionsClass.shapesImageId()) {
                                                1 -> {
                                                    dotDrawable = getDrawable(R.drawable.dot_droplet_icon) as Drawable
                                                }
                                                2 -> {
                                                    dotDrawable = getDrawable(R.drawable.dot_circle_icon) as Drawable
                                                }
                                                3 -> {
                                                    dotDrawable = getDrawable(R.drawable.dot_square_icon) as Drawable
                                                }
                                                4 -> {
                                                    dotDrawable = getDrawable(R.drawable.dot_squircle_icon) as Drawable
                                                }
                                                0 -> {
                                                    dotDrawable = functionsClass.appIcon(packageNames[startIdNotification]).mutate()
                                                }
                                            }
                                        }

                                        if (PublicVariable.themeLightDark) {
                                            dotDrawable.setTint(functionsClass.manipulateColor(functionsClass.extractVibrantColor(functionsClass.appIcon(packageNames[startIdNotification])), 1.30f))
                                        } else {
                                            dotDrawable.setTint(functionsClass.manipulateColor(functionsClass.extractVibrantColor(functionsClass.appIcon(packageNames[startIdNotification])), 0.50f))
                                        }

                                        notificationDots[startIdNotification].setImageDrawable(dotDrawable)
                                        notificationDots[startIdNotification].visibility = View.VISIBLE
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else if (intent.action == "Notification_Dot_No") {
                            val notificationPackage: String = intent.getStringExtra("NotificationPackage")
                            val startIdNotification: Int = mapPackageNameStartId[notificationPackage]!!

                            if (floatingShortcutsBinding.get(startIdNotification) != null) {
                                if (floatingShortcutsBinding.get(startIdNotification).root.isShown) {
                                    /*remove dot*/
                                    notificationDots[startIdNotification].visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }
                registerReceiver(broadcastReceiver, intentFilter)
            }

            if (getFileStreamPath(packageNames.get(startId) + "_" + "Notification" + "Package").exists()) {
                sendBroadcast(Intent("Notification_Dot").putExtra("NotificationPackage", packageNames.get(startId)))
            }
        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        securityFunctions = SecurityFunctions(applicationContext)

        openActions = OpenActions(applicationContext, functionsClass)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}