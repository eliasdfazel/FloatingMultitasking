/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/13/20 9:58 AM
 * Last modified 1/13/20 9:55 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.Functions

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import net.geekstools.floatshort.PRO.App_Unlimited_Shortcuts
import net.geekstools.floatshort.PRO.App_Unlimited_Shortcuts_Frequently
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.CheckPoint

class FunctionsClassRunServices(var context: Context) {

    fun runUnlimitedShortcutsService(packageName: String, className: String) {
        if (!Settings.canDrawOverlays(context)) {
            context.startActivity(Intent(context, CheckPoint::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }

        try {
            PublicVariable.floatingCounter++
            PublicVariable.shortcutsCounter++
            PublicVariable.FloatingShortcuts.add(PublicVariable.shortcutsCounter, packageName)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()

            PublicVariable.floatingCounter = PublicVariable.floatingCounter + 1
            PublicVariable.shortcutsCounter = PublicVariable.shortcutsCounter + 1
            PublicVariable.FloatingShortcuts.add(PublicVariable.shortcutsCounter, packageName)
        }

        Intent(context, App_Unlimited_Shortcuts::class.java).apply {
            putExtra("PackageName", packageName)
            putExtra("ClassName", className)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startService(this)
        }

        if (PublicVariable.floatingCounter == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, BindServices::class.java))
            } else {
                context.startService(Intent(context, BindServices::class.java))
            }
        }
    }

    fun runUnlimitedShortcutsServicePackage(packageName: String) {
        if (!Settings.canDrawOverlays(context)) {
            context.startActivity(Intent(context, CheckPoint::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }

        try {
            PublicVariable.floatingCounter++
            PublicVariable.shortcutsCounter++
            PublicVariable.FloatingShortcuts.add(PublicVariable.shortcutsCounter, packageName)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()

            PublicVariable.floatingCounter = PublicVariable.floatingCounter + 1
            PublicVariable.shortcutsCounter = PublicVariable.shortcutsCounter + 1
            PublicVariable.FloatingShortcuts.add(PublicVariable.shortcutsCounter, packageName)
        }

        Intent(context, App_Unlimited_Shortcuts_Frequently::class.java).apply {
            putExtra("PackageName", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startService(this)
        }

        if (PublicVariable.floatingCounter == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, BindServices::class.java))
            } else {
                context.startService(Intent(context, BindServices::class.java))
            }
        }
    }

    fun runUnlimitedShortcutsServiceFrequently(packageName: String) {
        if (!Settings.canDrawOverlays(context)) {
            context.startActivity(Intent(context, CheckPoint::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }

        try {
            PublicVariable.floatingCounter++
            PublicVariable.shortcutsCounter++
            PublicVariable.FloatingShortcuts.add(PublicVariable.shortcutsCounter, packageName)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()

            PublicVariable.floatingCounter = PublicVariable.floatingCounter + 1
            PublicVariable.shortcutsCounter = PublicVariable.shortcutsCounter + 1
            PublicVariable.FloatingShortcuts.add(PublicVariable.shortcutsCounter, packageName)
        }

        Intent(context, App_Unlimited_Shortcuts_Frequently::class.java).apply {
            putExtra("PackageName", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startService(this)
        }

        if (PublicVariable.floatingCounter == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, BindServices::class.java))
            } else {
                context.startService(Intent(context, BindServices::class.java))
            }
        }
    }
}