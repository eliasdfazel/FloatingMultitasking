/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 6:17 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Automation.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Utils.Functions.Debug
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class ColorReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        extractColor(context)

        Debug.PrintDebug("Wallpaper Changed")
    }

    private fun extractColor(context: Context) = CoroutineScope(Dispatchers.IO).launch {

        val functionsClass = FunctionsClass(context)

        functionsClass.extractWallpaperColor()

    }
}