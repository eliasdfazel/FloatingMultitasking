package net.geekstools.floatshort.PRO.Shortcuts.FloatingServices

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.IBinder
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.dynamicanimation.animation.FlingAnimation
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Widgets.Utils.FunctionsClassWidgets
import net.geekstools.floatshort.PRO.databinding.FloatingShortcutsBinding
import net.geekstools.imageview.customshapes.ShapesImage

class AppUnlimitedShortcutsXYZ : Service() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }
    private lateinit var functionsClassWidgets: FunctionsClassWidgets


    private lateinit var windowManager: WindowManager


    private val layoutParams: ArrayList<WindowManager.LayoutParams> = ArrayList<WindowManager.LayoutParams>()
    private val StickyEdgeParams: ArrayList<WindowManager.LayoutParams> = ArrayList<WindowManager.LayoutParams>()

    private lateinit var moveDetection: WindowManager.LayoutParams

    private object XY {
        var xPosition = 0
        var yPosition:Int = 0

        var xInitial:Int = 13
        var yInitial:Int = 13

        var xMove:Int = 0
        var yMove:Int = 0
    }

    private val activityInformation: ArrayList<ActivityInfo> = ArrayList<ActivityInfo>()


    private val packageNames: ArrayList<String> = ArrayList<String>()
    private val classNames: ArrayList<String> = ArrayList<String>()

    private val appIcon: ArrayList<Drawable> = ArrayList<Drawable>()
    private val iconColor: ArrayList<Int> = ArrayList<Int>()


    private val openPermit: ArrayList<Boolean> = ArrayList<Boolean>()
    private val movePermit: ArrayList<Boolean> = ArrayList<Boolean>()
    private val removePermit: ArrayList<Boolean> = ArrayList<Boolean>()

    private val touchingDelay: ArrayList<Boolean> = ArrayList<Boolean>()

    private val stickedToEdge: ArrayList<Boolean> = ArrayList<Boolean>()


    private val shapedIcon: ArrayList<ShapesImage> = ArrayList<ShapesImage>()
    private val controlIcon: ArrayList<ShapesImage> = ArrayList<ShapesImage>()
    private val notificationDot: ArrayList<ShapesImage> = ArrayList<ShapesImage>()


    lateinit var delayRunnable: Runnable
    lateinit var getBackRunnable: Runnable
    lateinit var runnablePressHold: Runnable

    var delayHandler = Handler()
    var getBackHandler: Handler = Handler()
    var handlerPressHold: Handler = Handler()


    lateinit var broadcastReceiver: BroadcastReceiver


    lateinit var sharedPrefPosition: SharedPreferences


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
                                    functionsClass.handleOrientationPortrait(classNames[J], layoutParams[J].height))

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
                                    functionsClass.handleOrientationLandscape(classNames[J], layoutParams[J].height))

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

        val startId = (serviceStartId - 1)
        floatingShortcutsBinding.add(startId, FloatingShortcutsBinding.inflate(layoutInflater))



        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        functionsClassWidgets = FunctionsClassWidgets(applicationContext)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}