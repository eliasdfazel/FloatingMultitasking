/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.Utils

import android.content.Context
import android.content.Intent
import android.view.WindowManager
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.UI.Splash.FloatingSplash

class OpenActions(private val context: Context, private val functionsClassLegacy: FunctionsClassLegacy) {

    fun startProcess(packageName: String, className: String,
                     layoutParams: WindowManager.LayoutParams) {

        if (functionsClassLegacy.splashReveal()) {

            val splashReveal = Intent(context, FloatingSplash::class.java).apply {
                putExtra("packageName", packageName)
                putExtra("className", className)

                putExtra("X", layoutParams.x)
                putExtra("Y", layoutParams.y)

                putExtra("HW", layoutParams.width)
            }

            context.startService(splashReveal)

        } else {

            if (functionsClassLegacy.FreeForm()) {

                functionsClassLegacy.openApplicationFreeForm(
                        packageName,
                        className,
                        layoutParams.x,
                        functionsClassLegacy.displayX() / 2,
                        layoutParams.y,
                        functionsClassLegacy.displayY() / 2
                )
            } else {

                functionsClassLegacy
                        .appsLaunchPad(packageName, className)
            }
        }
    }

    fun startProcess(packageName: String,
                     layoutParams: WindowManager.LayoutParams) {

        if (functionsClassLegacy.splashReveal()) {

            val splashReveal = Intent(context, FloatingSplash::class.java).apply {
                putExtra("packageName", packageName)

                putExtra("X", layoutParams.x)
                putExtra("Y", layoutParams.y)

                putExtra("HW", layoutParams.width)
            }

            context.startService(splashReveal)

        } else {

            if (functionsClassLegacy.FreeForm()) {

                functionsClassLegacy.openApplicationFreeForm(
                        packageName,
                        layoutParams.x,
                        functionsClassLegacy.displayX() / 2,
                        layoutParams.y,
                        functionsClassLegacy.displayY() / 2
                )
            } else {

                functionsClassLegacy
                        .appsLaunchPad(packageName)
            }
        }
    }

    fun startProcess(packageName: String, className: String,
                     xPosition: Int, yPosition: Int, HW: Int) {

        if (functionsClassLegacy.splashReveal()) {

            val splashReveal = Intent(context, FloatingSplash::class.java).apply {
                putExtra("packageName", packageName)
                putExtra("className", className)

                putExtra("X", xPosition)
                putExtra("Y", yPosition)

                putExtra("HW", HW)
            }

            context.startService(splashReveal)

        } else {

            if (functionsClassLegacy.FreeForm()) {

                functionsClassLegacy.openApplicationFreeForm(
                    packageName,
                    className,
                    xPosition,
                    functionsClassLegacy.displayX() / 2,
                    yPosition,
                    functionsClassLegacy.displayY() / 2
                )
            } else {

                functionsClassLegacy
                    .appsLaunchPad(packageName, className)

            }
        }
    }

}