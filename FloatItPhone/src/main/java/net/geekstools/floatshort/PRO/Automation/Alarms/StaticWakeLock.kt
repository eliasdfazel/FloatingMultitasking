/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/21/20 5:55 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Automation.Alarms

import android.content.Context
import android.os.PowerManager

fun invokeSystem(context: Context) {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    val wakeLock: PowerManager.WakeLock? = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, context.packageName.javaClass.simpleName)
    wakeLock?.let {
        it.acquire(10 * 60 * 1000L /*10 minutes*/)
    }
}