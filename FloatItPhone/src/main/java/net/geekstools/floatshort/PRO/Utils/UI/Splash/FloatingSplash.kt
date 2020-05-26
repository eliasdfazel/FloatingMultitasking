/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/25/20 6:36 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.UI.Splash

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.RevealSplashBinding
import net.geekstools.imageview.customshapes.ShapesImage


class FloatingSplash : Service() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val windowManager: WindowManager by lazy {
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private val layoutParams: WindowManager.LayoutParams by lazy {
        functionsClass.splashRevealParams(
                0,
                0
        )
    }

    private val layoutInflater: LayoutInflater by lazy {
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    lateinit var shapedIcon: ShapesImage


    companion object {
        var xPositionRemoval: Int = 0
        var yPositionRemoval:Int = 0

        var HeightWidthRemoval:Int = 0
    }

    var xPosition = 0
    var yPosition:Int = 0

    var appIconColor:Int = 0

    var statusBarHeight = 0
    var navBarHeight:Int = 0

    private lateinit var appPackageName: String
    private var appClassName: String? = null

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName());
    }

    private lateinit var revealSplashBinding: RevealSplashBinding

    val floatingSplashRemoval: FloatingSplashRemoval by lazy {
        FloatingSplashRemoval(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(initialIntent: Intent?, flags: Int, startId: Int): Int {

        initialIntent?.let { intent ->


            navBarHeight = 0
            val resourceIdNav = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceIdNav > 0) {
                navBarHeight = resources.getDimensionPixelSize(resourceIdNav)
            }
            statusBarHeight = 0
            val resourceIdStatus = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceIdStatus > 0) {
                statusBarHeight = resources.getDimensionPixelSize(resourceIdStatus)
            }

            appPackageName = intent.getStringExtra("packageName")
            var appIcon = getDrawable(R.drawable.ic_launcher_balloon)

            if (intent.hasExtra("className")) {

                appClassName = intent.getStringExtra("className")

                appIcon = if (functionsClass.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(packageManager.getActivityInfo(ComponentName(appPackageName, appClassName!!), 0), functionsClass.shapedAppIcon(appPackageName).mutate()) else functionsClass.shapedAppIcon(appPackageName).mutate()

            } else {

                appIcon = if (functionsClass.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(appPackageName, functionsClass.shapedAppIcon(appPackageName).mutate()) else functionsClass.shapedAppIcon(appPackageName).mutate()

            }

            appIconColor = functionsClass.extractDominantColor(appIcon)
            val HW = intent.getIntExtra("HW", 0)
            val xPosition = intent.getIntExtra("X", 0)
            val yPosition = intent.getIntExtra("Y", 0)

            FloatingSplash.xPositionRemoval = xPosition
            FloatingSplash.yPositionRemoval = yPosition + statusBarHeight
            FloatingSplash.HeightWidthRemoval = HW

            revealSplashBinding = RevealSplashBinding.inflate(layoutInflater)
            shapedIcon = functionsClass.initShapesImage(revealSplashBinding.shapedIcon)
            val layoutParamsRelativeLayout = RelativeLayout.LayoutParams(
                    HW,
                    HW
            )
            shapedIcon.x = xPosition.toFloat()
            shapedIcon.y = yPosition + statusBarHeight.toFloat()
            shapedIcon.layoutParams = layoutParamsRelativeLayout
            shapedIcon.setImageDrawable(appIcon)

            windowManager.addView(revealSplashBinding.root, layoutParams)

            val confirmButtonLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            floatingSplashRemoval.layoutParams = confirmButtonLayoutParams

            floatingSplashRemoval.visibility = View.INVISIBLE
            revealSplashBinding.rootView.addView(floatingSplashRemoval)

            Handler().postDelayed({

                try {

                    functionsClass.circularRevealSplashScreen(
                            revealSplashBinding.splashView,
                            shapedIcon,
                            xPosition,
                            yPosition + statusBarHeight,
                            appIconColor,
                            appPackageName,
                            appClassName,
                            true
                    )

                } catch (e: Exception) {
                    e.printStackTrace()

                    if (appClassName != null) {
                        functionsClass.appsLaunchPad(appPackageName, appClassName)
                    } else {
                        functionsClass.appsLaunchPad(appPackageName)
                    }
                }

            }, 159)

            revealSplashBinding.root.setOnClickListener {

                val homeScreen = Intent(Intent.ACTION_MAIN)
                homeScreen.addCategory(Intent.CATEGORY_HOME)
                homeScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(homeScreen)

                Handler().postDelayed({
                    stopSelf()
                }, 159)

            }

        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        if (functionsClass.customIconsEnable()) {

            loadCustomIcons.load()

        }

    }

    override fun onDestroy() {

        try {

            layoutParams.windowAnimations = android.R.style.Animation_Dialog
            windowManager.updateViewLayout(revealSplashBinding.root, layoutParams)

            if (functionsClass.UsageStatsEnabled()) {

                floatingSplashRemoval.floatingSplashRemoval()

                Handler().postDelayed({
                    windowManager.removeViewImmediate(revealSplashBinding.root)
                }, 888)

            } else {

                functionsClass.circularRevealSplashScreen(
                        revealSplashBinding.splashView,
                        shapedIcon,
                        xPosition,
                        yPosition + statusBarHeight,
                        appIconColor,
                        appPackageName,
                        appClassName,
                        false
                )
                Handler().postDelayed({

                    windowManager.removeViewImmediate(revealSplashBinding.root)

                }, 133)

            }

        } catch (e: Exception) {
            e.printStackTrace()

            windowManager.removeViewImmediate(revealSplashBinding.root)

        }
    }
}