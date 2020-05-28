/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/28/20 3:53 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation.Alarms.Utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import net.geekstools.floatshort.PRO.Automation.Alarms.AlarmAlertBroadcastReceiver
import net.geekstools.floatshort.PRO.Automation.Alarms.SetupAlarms
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import java.util.*

class FunctionsClassAlarms (private val context: Context) {

    fun initialAlarm(newAlarmTime: Calendar, setTime: String, position: Int = 0) {

        val alarmIntent = Intent(context, AlarmAlertBroadcastReceiver::class.java)
        alarmIntent.putExtra("time", setTime)
        alarmIntent.putExtra("position", position)

        val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        newAlarmTime.add(Calendar.DAY_OF_MONTH, 1)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

        alarmManager?.let {

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    newAlarmTime.timeInMillis,  /*AlarmManager.INTERVAL_DAY*/
                    86400000,
                    pendingIntent)
        }

        context.stopService(Intent(context, SetupAlarms::class.java))
        context.stopService(Intent(context, BindServices::class.java))

        FunctionsClassDebug.PrintDebug("*** " + newAlarmTime.time + " ***")
    }
}