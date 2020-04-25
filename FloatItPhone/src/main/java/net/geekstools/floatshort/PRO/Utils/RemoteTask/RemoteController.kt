/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/25/20 5:48 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.RemoteTask

import android.app.Service
import android.content.Intent
import android.os.IBinder
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassIO
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryAll
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Remove.RemoveAll

class RemoteController : Service() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val functionsClassIO: FunctionsClassIO by lazy {
        FunctionsClassIO(applicationContext)
    }

    object COMMANDS {
        const val CancelRemote = "CancelRemote"
        const val RecoverAll = "RecoverAll"
        const val RemoveAll = "RemoveAll"
        const val Sticky_Edge = "Sticky_Edge"
        const val Sticky_Edge_No = "Sticky_Edge_No"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            val remoteControllerCommand = intent.getStringExtra("RemoteController")

            when (intent.getStringExtra("RemoteController")) {
                RemoteController.COMMANDS.CancelRemote -> {

                    if (functionsClassIO.automationFeatureEnable() || functionsClass.ControlPanel()) {
                        /**/
                    } else {

                        stopService(Intent(applicationContext, BindServices::class.java))

                    }
                }
                RemoteController.COMMANDS.RecoverAll -> {

                    startService(Intent(applicationContext, RecoveryAll::class.java))

                }
                RemoteController.COMMANDS.RemoveAll -> {

                    startService(Intent(applicationContext, RemoveAll::class.java))

                }
                RemoteController.COMMANDS.Sticky_Edge -> {

                    sendBroadcast(Intent("Sticky_Edge"))

                }
                RemoteController.COMMANDS.Sticky_Edge_No -> {

                    sendBroadcast(Intent("Sticky_Edge_No"))

                }
                else -> {

                }
            }
        }

        stopSelf()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {

        return null
    }
}