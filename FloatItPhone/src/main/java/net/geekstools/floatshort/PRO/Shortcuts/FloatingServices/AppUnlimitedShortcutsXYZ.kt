package net.geekstools.floatshort.PRO.Shortcuts.FloatingServices

import android.app.Service
import android.content.*
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.IBinder
import android.util.TypedValue
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.dynamicanimation.animation.FlingAnimation
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
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

        intent?.run {

            packageNames[startId] = this@run.getStringExtra("PackageName")

            if (!functionsClass.appIsInstalled(packageNames[startId])) {

                return START_NOT_STICKY
            }
            /*
             *
             *
             * Remove All Checkpoint
             *
             *
             */
            if (this@run.hasExtra(getString(R.string.remove_all_floatings))) {
                if (this@run.getStringExtra(getString(R.string.remove_all_floatings)) == getString(R.string.remove_all_floatings)) {



                    stopSelf()

                    return START_NOT_STICKY
                }
            }

            classNames.add(startId, intent.getStringExtra("ClassName"))
            activityInformation.add(startId, packageManager.getActivityInfo(ComponentName(packageNames[startId], classNames[startId]), 0))

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

            touchingDelay.add(startId, false)
            stickedToEdge.add(startId, false)

            mapPackageNameStartId.put(classNames[startId], startId)

            /*Update Floating Shortcuts Database*/
            functionsClass.saveUnlimitedShortcutsService(packageNames[startId])
            functionsClass.updateRecoverShortcuts()
            /*Update Floating Shortcuts Database*/

            appIcons.add(startId, functionsClass.shapedAppIcon(activityInformation[startId]))
            iconColors.add(startId, functionsClass.extractDominantColor(functionsClass.appIcon(activityInformation[startId])))

            shapedIcons[startId].setImageDrawable(if (functionsClass.customIconsEnable()) {
                loadCustomIcons.getDrawableIconForPackage(packageNames[startId], functionsClass.shapedAppIcon(activityInformation[startId]))
            } else {
                functionsClass.shapedAppIcon(activityInformation[startId])
            })

            sharedPrefPosition = getSharedPreferences(classNames[startId], Context.MODE_PRIVATE)

            PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39)
            PublicVariable.HW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size.toFloat(), applicationContext.resources.displayMetrics).toInt()

            XY.xInitial = XY.xInitial + 13
            XY.yInitial = XY.yInitial + 13
            XY.xPosition = sharedPrefPosition.getInt("X", XY.xInitial)
            XY.yPosition = sharedPrefPosition.getInt("Y", XY.yInitial)
            layoutParams[startId] = functionsClass.normalLayoutParams(PublicVariable.HW, XY.xPosition, XY.yPosition)

            try {
                floatingShortcutsBinding.get(startId).root.setTag(startId)
                windowManager.addView(floatingShortcutsBinding.get(startId).root, layoutParams[startId])
            } catch (e: WindowManager.BadTokenException) {
                e.printStackTrace()
            }

            XY.xMove = XY.xPosition
            XY.yMove = XY.yPosition


        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        functionsClassWidgets = FunctionsClassWidgets(applicationContext)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}