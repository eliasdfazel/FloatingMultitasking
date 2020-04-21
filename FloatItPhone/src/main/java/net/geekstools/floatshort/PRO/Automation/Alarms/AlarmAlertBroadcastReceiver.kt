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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class AlarmAlertBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        intent?.let {
            if (context != null) {
                invokeSystem(context)

                val functionsClass = FunctionsClass(context)

                val setTime = intent.getStringExtra("time")
                val alarmPosition = intent.getIntExtra("position", 0)

                PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39)
                PublicVariable.HW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size.toFloat(), context.resources.displayMetrics).toInt()

                val alaramedPackageNames = functionsClass.readFileLine(setTime)

                if (alaramedPackageNames.isNotEmpty()) {

                    alaramedPackageNames.forEach {  aAlaramedPackageName ->

                        if (aAlaramedPackageName.contains("APP")) {

                            functionsClass
                                    .runUnlimitedTime(aAlaramedPackageName.replace("APP", ""))

                        } else if (aAlaramedPackageName.contains("CATEGORY")) {

                            functionsClass
                                    .runUnlimitedFolderTime(aAlaramedPackageName.replace("CATEGORY", ""),
                                            functionsClass.readFileLine(aAlaramedPackageName.replace("CATEGORY", "")))
                        }
                    }

                    Intent(context, SetupAlarms::class.java).apply {
                        putExtra("time", setTime)
                        putExtra("position", alarmPosition)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(this@apply)
                        } else {
                            context.startService(this@apply)
                        }
                    }
                }
            }
        }
    }
}

