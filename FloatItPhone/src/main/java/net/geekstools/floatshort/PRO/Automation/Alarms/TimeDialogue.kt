/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/28/20 2:45 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Automation.Alarms

import android.app.Activity
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import net.geekstools.floatshort.PRO.Automation.Alarms.Utils.FunctionsClassAlarms
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug.Companion.PrintDebug
import java.util.*

class TimeDialogue : Activity() {

    private lateinit var timePickerDialog: TimePickerDialog

    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)

        val content: String? = intent.getStringExtra("content")
        val type: String? = intent.getStringExtra("type")

        if (content != null
                && type != null) {

            val functionsClass = FunctionsClass(applicationContext)
            val functionsClassAlarm = FunctionsClassAlarms(applicationContext)

            val newAlarmTime = Calendar.getInstance()

            timePickerDialog = TimePickerDialog(this@TimeDialogue, OnTimeSetListener { timePicker, hours, minutes ->

                newAlarmTime.set(Calendar.HOUR_OF_DAY, hours)
                newAlarmTime.set(Calendar.MINUTE, minutes)
                newAlarmTime.set(Calendar.SECOND, 13)

                val setTime = "$hours:$minutes"
                PrintDebug("*** $setTime")

                functionsClass.saveFile(
                        "$content.Time",
                        setTime)

                functionsClass.removeLine(".times.clocks", setTime)
                functionsClass.saveFileAppendLine(
                        ".times.clocks",
                        setTime)

                functionsClass.saveFileAppendLine(
                        setTime,
                        content + type)

                functionsClassAlarm.initialAlarm(newAlarmTime, setTime)

                timePickerDialog.dismiss()

            }, Calendar.getInstance()[Calendar.HOUR_OF_DAY], Calendar.getInstance()[Calendar.MINUTE], true)

            timePickerDialog.setCancelable(false)
            timePickerDialog.show()

            timePickerDialog.setOnDismissListener {

                this@TimeDialogue.finish()
            }

        }

    }
}