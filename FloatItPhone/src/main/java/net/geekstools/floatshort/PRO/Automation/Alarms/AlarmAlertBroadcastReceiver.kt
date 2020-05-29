/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/28/20 9:12 PM
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
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassPreferences
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassRunServices
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class AlarmAlertBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        intent?.let {
            if (context != null) {
                invokeSystem(context)

                val functionsClassIO = FunctionsClassIO(context)
                val functionsClassRunServices = FunctionsClassRunServices(context)
                val functionsClassPreferences = FunctionsClassPreferences(context)

                val setTime = intent.getStringExtra("time")
                val alarmPosition = intent.getIntExtra("position", 0)

                PublicVariable.floatingSizeNumber = functionsClassPreferences.readDefaultPreference("floatingSize", 39)
                PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), context.resources.displayMetrics).toInt()

                val alarmedPackageNames = functionsClassIO.readFileLinesAsArray(setTime)

                if (!alarmedPackageNames.isNullOrEmpty()) {

                    alarmedPackageNames.forEach { aAlarmedPackageNames ->

                        if (aAlarmedPackageNames.contains("APP")) {

                            functionsClassRunServices
                                    .runUnlimitedShortcutsForTime(aAlarmedPackageNames.replace("APP", ""))

                        } else if (aAlarmedPackageNames.contains("CATEGORY")) {

                            functionsClassIO.readFileLinesAsArray(aAlarmedPackageNames.replace("CATEGORY", ""))?.let {

                                functionsClassRunServices
                                        .runUnlimitedFoldersForTime(aAlarmedPackageNames.replace("CATEGORY", ""),
                                                it)
                            }
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

