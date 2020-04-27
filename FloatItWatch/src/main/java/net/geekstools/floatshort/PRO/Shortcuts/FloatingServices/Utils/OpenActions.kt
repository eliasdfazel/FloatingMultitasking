/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 6:49 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.Utils

import android.content.Context
import android.content.Intent
import android.view.WindowManager
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.UI.FloatingSplash

class OpenActions(private val context: Context, private val functionsClass: FunctionsClass) {

    fun startProcess(packageName: String, className: String,
                     layoutParams: WindowManager.LayoutParams) {

        if (functionsClass.splashReveal()) {

            val splashReveal = Intent(context, FloatingSplash::class.java).apply {
                putExtra("packageName", packageName)
                putExtra("className", className)

                putExtra("X", layoutParams.x)
                putExtra("Y", layoutParams.y)

                putExtra("HW", layoutParams.width)
            }

            context.startService(splashReveal)

        } else {

            functionsClass
                    .appsLaunchPad(packageName, className)
        }
    }

    fun startProcess(packageName: String,
                     layoutParams: WindowManager.LayoutParams) {

        if (functionsClass.splashReveal()) {

            val splashReveal = Intent(context, FloatingSplash::class.java).apply {
                putExtra("packageName", packageName)

                putExtra("X", layoutParams.x)
                putExtra("Y", layoutParams.y)

                putExtra("HW", layoutParams.width)
            }

            context.startService(splashReveal)

        } else {

            functionsClass
                    .appsLaunchPad(packageName)
        }
    }
}