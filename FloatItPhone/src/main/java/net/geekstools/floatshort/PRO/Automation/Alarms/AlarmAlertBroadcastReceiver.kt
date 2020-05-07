/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/7/20 1:46 PM
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

                PublicVariable.floatingSizeNumber = functionsClass.readDefaultPreference("floatingSize", 39)
                PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), context.resources.displayMetrics).toInt()

                val alarmedPackageNames = functionsClass.readFileLine(setTime)

                if (!alarmedPackageNames.isNullOrEmpty()) {

                    alarmedPackageNames.forEach { aAlarmedPackageNames ->

                        if (aAlarmedPackageNames.contains("APP")) {

                            functionsClass
                                    .runUnlimitedTime(aAlarmedPackageNames.replace("APP", ""))

                        } else if (aAlarmedPackageNames.contains("CATEGORY")) {

                            functionsClass
                                    .runUnlimitedFolderTime(aAlarmedPackageNames.replace("CATEGORY", ""),
                                            functionsClass.readFileLine(aAlarmedPackageNames.replace("CATEGORY", "")))
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

