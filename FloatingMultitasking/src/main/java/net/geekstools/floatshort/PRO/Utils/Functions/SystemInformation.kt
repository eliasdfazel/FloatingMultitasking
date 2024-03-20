/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 6:16 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.app.ActivityManager
import android.content.Context
import android.os.Build

class SystemInformation (private val context: Context) {

    object Requirements {
        const val RequiredMemory: Long = 4000000000
    }
    object CPU_MODELS {
        const val Qualcomm: String = "qcom"
    }

    /**
     * Check Device Information & Apply Lite Preferences For Specific Device Model
     **/
    fun checkDeviceInformation() {

        when (getCpuModel()) {
            CPU_MODELS.Qualcomm -> {

            }
            else -> {

                if (!checkRequiredMemory()
                        && !context.getFileStreamPath(".LitePreferenceCheckpoint").exists()) {

                    PreferencesIO(context)
                            .switchLitePreference(true).let { success ->

                            }
                }
            }
        }
    }

    fun getCpuModel() : String {

        return Build.HARDWARE
    }

    fun getDeviceManufacturer() : String {

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