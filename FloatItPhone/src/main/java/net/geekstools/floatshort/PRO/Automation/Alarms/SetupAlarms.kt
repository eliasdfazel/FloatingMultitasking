/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/28/20 3:22 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation.Alarms

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import net.geekstools.floatshort.PRO.Automation.Alarms.Utils.FunctionsClassAlarms
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassIO
import java.util.*
import kotlin.collections.ArrayList

class SetupAlarms : Service() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val functionsClassAlarms: FunctionsClassAlarms by lazy {
        FunctionsClassAlarms(applicationContext)
    }

    private val functionsClassIO: FunctionsClassIO by lazy {
        FunctionsClassIO(applicationContext)
    }

    private val newAlarmTime = Calendar.getInstance()

    private lateinit var nextSetTime: String
    private var nextPosition = 0

    private var timesClocks: ArrayList<String>? = ArrayList<String>()

    private var hoursDigits = 0
    private var minutesDigits:Int = 0

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {

            val setTime = intent.getStringExtra("time")
            val packagePosition = intent.getIntExtra("position", -1)

            timesClocks = functionsClassIO.readFileLinesAsList(".times.clocks")

            timesClocks?.let { timesClocks ->

                if ((packagePosition + 1) == timesClocks.size) {

                    //last one reset to first line
                    val time = timesClocks[0].split(":").toTypedArray()
                    hoursDigits = time[0].toInt()
                    minutesDigits = time[1].toInt()

                    newAlarmTime[Calendar.HOUR_OF_DAY] = hoursDigits
                    newAlarmTime[Calendar.MINUTE] = minutesDigits
                    newAlarmTime[Calendar.SECOND] = 0

                    nextSetTime = timesClocks[0]
                    nextPosition = 0

                } else {

                    //set the next line
                    try {

                        val time = timesClocks[packagePosition + 1].split(":").toTypedArray()
                        hoursDigits = time[0].toInt()
                        minutesDigits = time[1].toInt()

                        newAlarmTime[Calendar.HOUR_OF_DAY] = hoursDigits
                        newAlarmTime[Calendar.MINUTE] = minutesDigits
                        newAlarmTime[Calendar.SECOND] = 13

                        nextSetTime = timesClocks[packagePosition + 1]
                        nextPosition = packagePosition + 1

                    } catch (e: Exception) {
                        e.printStackTrace()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            stopForeground(STOP_FOREGROUND_REMOVE)
                            stopForeground(true)
                        }

                        this@SetupAlarms.stopSelf()

                        return START_NOT_STICKY
                    }

                }

                functionsClassAlarms.initialAlarm(newAlarmTime, nextSetTime, nextPosition)

                FunctionsClassDebug.PrintDebug("*** Setting Up New/Next Alarm ***")

            }

        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            startForeground(333, functionsClass.bindServiceNotification(), STOP_FOREGROUND_REMOVE)

        } else {

            startForeground(333, functionsClass.bindServiceNotification())

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            startForegroundService(Intent(applicationContext, BindServices::class.java))

        } else {

            startService(Intent(applicationContext, BindServices::class.java))

        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopForeground(true)
        }

        this@SetupAlarms.stopSelf()
    }
}