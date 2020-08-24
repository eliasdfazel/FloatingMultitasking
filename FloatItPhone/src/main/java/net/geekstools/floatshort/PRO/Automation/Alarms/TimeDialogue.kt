/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 6:13 AM
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
import net.geekstools.floatshort.PRO.Utils.Functions.Debug.Companion.PrintDebug
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import java.util.*

class TimeDialogue : Activity() {

    private val fileIO: FileIO by lazy {
        FileIO(applicationContext)
    }

    private lateinit var timePickerDialog: TimePickerDialog

    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)

        val content: String? = intent.getStringExtra("content")
        val type: String? = intent.getStringExtra("type")

        if (content != null
                && type != null) {

            val functionsClassAlarm = FunctionsClassAlarms(applicationContext)

            val newAlarmTime = Calendar.getInstance()

            timePickerDialog = TimePickerDialog(this@TimeDialogue, OnTimeSetListener { timePicker, hours, minutes ->

                newAlarmTime.set(Calendar.HOUR_OF_DAY, hours)
                newAlarmTime.set(Calendar.MINUTE, minutes)
                newAlarmTime.set(Calendar.SECOND, 13)

                val setTime = "$hours:$minutes"
                PrintDebug("*** $setTime")

                fileIO.saveFile(
                        "$content.Time",
                        setTime)

                fileIO.removeLine(".times.clocks", setTime)
                fileIO.saveFileAppendLine(
                        ".times.clocks",
                        setTime)

                fileIO.saveFileAppendLine(
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