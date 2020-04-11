/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 11:33 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.Checkpoint
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForApplications
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForFrequentlyApplications

class FunctionsClassRunServices(var context: Context) {

    fun runUnlimitedShortcutsService(packageName: String, className: String) {
        if (!Settings.canDrawOverlays(context)) {
            context.startActivity(Intent(context, Checkpoint::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }

        try {
            PublicVariable.allFloatingCounter++
            PublicVariable.FloatingShortcutsCounter++
            PublicVariable.FloatingShortcutsList.add(PublicVariable.FloatingShortcutsCounter, packageName)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()

            PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter + 1
            PublicVariable.FloatingShortcutsCounter = PublicVariable.FloatingShortcutsCounter + 1
            PublicVariable.FloatingShortcutsList.add(PublicVariable.FloatingShortcutsCounter, packageName)
        }

        Intent(context, FloatingShortcutsForApplications::class.java).apply {
            putExtra("PackageName", packageName)
            putExtra("ClassName", className)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startService(this)
        }

        if (PublicVariable.allFloatingCounter == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, BindServices::class.java))
            } else {
                context.startService(Intent(context, BindServices::class.java))
            }
        }
    }

    fun runUnlimitedShortcutsServicePackage(packageName: String) {
        if (!Settings.canDrawOverlays(context)) {
            context.startActivity(Intent(context, Checkpoint::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }

        try {
            PublicVariable.allFloatingCounter++
            PublicVariable.FloatingShortcutsCounter++
            PublicVariable.FloatingShortcutsList.add(PublicVariable.FloatingShortcutsCounter, packageName)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()

            PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter + 1
            PublicVariable.FloatingShortcutsCounter = PublicVariable.FloatingShortcutsCounter + 1
            PublicVariable.FloatingShortcutsList.add(PublicVariable.FloatingShortcutsCounter, packageName)
        }

        Intent(context, FloatingShortcutsForFrequentlyApplications::class.java).apply {
            putExtra("PackageName", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startService(this)
        }

        if (PublicVariable.allFloatingCounter == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, BindServices::class.java))
            } else {
                context.startService(Intent(context, BindServices::class.java))
            }
        }
    }

    fun runUnlimitedShortcutsServiceFrequently(packageName: String) {
        if (!Settings.canDrawOverlays(context)) {
            context.startActivity(Intent(context, Checkpoint::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }

        try {
            PublicVariable.allFloatingCounter++
            PublicVariable.FloatingShortcutsCounter++
            PublicVariable.FloatingShortcutsList.add(PublicVariable.FloatingShortcutsCounter, packageName)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()

            PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter + 1
            PublicVariable.FloatingShortcutsCounter = PublicVariable.FloatingShortcutsCounter + 1
            PublicVariable.FloatingShortcutsList.add(PublicVariable.FloatingShortcutsCounter, packageName)
        }

        Intent(context, FloatingShortcutsForFrequentlyApplications::class.java).apply {
            putExtra("PackageName", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startService(this)
        }

        if (PublicVariable.allFloatingCounter == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, BindServices::class.java))
            } else {
                context.startService(Intent(context, BindServices::class.java))
            }
        }
    }
}