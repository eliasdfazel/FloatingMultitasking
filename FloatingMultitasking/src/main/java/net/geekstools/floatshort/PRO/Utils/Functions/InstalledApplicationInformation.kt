/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/25/20 4:42 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable

class InstalledApplicationInformation(private val context: Context) {

    fun applicatioName(packageName: String): String {

        return try {
            val packageManager = context.packageManager
            val applicationInfo = context.packageManager.getApplicationInfo(packageName, 0)

            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            e.printStackTrace()

            "null"
        }
    }

    fun activityLabel(activityInfo: ActivityInfo): String {

        return try {
            activityInfo.loadLabel(context.packageManager).toString()
        } catch (e: Exception) {
            e.printStackTrace()

            applicatioName(activityInfo.packageName)
        }
    }

    fun applicationVersionCode(packageName: String): Int {

        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                packageInfo.versionCode
            }
        } catch (e: Exception) {
            e.printStackTrace()

            1
        }
    }

    fun applicationIcon(packageName: String): Drawable? {
        var icon: Drawable? = null

        try {
            val packageManager = context.packageManager
            icon = packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (icon == null) {
                try {
                    val packManager = context.packageManager
                    icon = packManager.defaultActivityIcon
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return icon
    }

    fun applicationIcon(activityInfo: ActivityInfo): Drawable? {
        var icon: Drawable? = null
        try {
            icon = activityInfo.loadIcon(context.packageManager)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            if (icon == null) {
                try {
                    val packManager = context.packageManager
                    icon = packManager.defaultActivityIcon
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        return icon
    }

}