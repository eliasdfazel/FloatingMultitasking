/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/24/20 8:41 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.app.ActivityManager
import android.content.Context
import android.os.Build

class FunctionsClassSystemInformation (private val context: Context) {

    object Requirements {
        const val RequiredMemory: Long = 4000000000
    }
    object CPU_MODELS {
        const val Qualcomm: String = "qcom"
    }

    /**
     * Check Device Information & Apply Lite Preferences For Specific Device Model
     **/
    fun checkDevieInformation() {

        when (getCpuModel()) {
            CPU_MODELS.Qualcomm -> {

            }
            else -> {

                if (!checkRequiredMemory()
                        && !context.getFileStreamPath(".LitePreferenceCheckpoint").exists()) {

                    FunctionsClassPreferences(context)
                            .switchLitePreference(true).let { success ->

                            }
                }
            }
        }
    }

    fun getCpuModel() : String {
        FunctionsClassDebug.PrintDebug("*** ${Build.HARDWARE} ***")

        return Build.HARDWARE
    }

    fun getDeviceManufacturer() : String {
        FunctionsClassDebug.PrintDebug("*** ${Build.MANUFACTURER} ***")

        return Build.MANUFACTURER
    }

    fun checkRequiredMemory() : Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?

        val memoryInfo = ActivityManager.MemoryInfo()

        return if (activityManager != null) {

            activityManager.getMemoryInfo(memoryInfo)

            //Return
            memoryInfo.totalMem >= Requirements.RequiredMemory || !memoryInfo.lowMemory

        } else {

            false

        }
    }
}