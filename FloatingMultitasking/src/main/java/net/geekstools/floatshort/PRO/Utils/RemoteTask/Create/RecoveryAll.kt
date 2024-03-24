/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.app.ActivityOptions
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy

class RecoveryAll : Service() {

    private val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (functionsClassLegacy.securityServicesSubscribed()) {

            SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                override fun authenticatedFloatIt(extraInformation: Bundle?) {
                    super.authenticatedFloatIt(extraInformation)
                    Log.d(this@RecoveryAll.javaClass.simpleName, "AuthenticatedFloatIt")

                    startService(Intent(applicationContext, RecoveryShortcuts::class.java).putExtra("AuthenticatedFloatIt", true))
                    startService(Intent(applicationContext, RecoveryFolders::class.java).putExtra("AuthenticatedFloatIt", true))
                    startService(Intent(applicationContext, RecoveryWidgets::class.java).putExtra("AuthenticatedFloatIt", true))

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopForeground(true)
                    }

                    stopSelf()
                }

                override fun failedAuthenticated() {
                    super.failedAuthenticated()
                    Log.d(this@RecoveryAll.javaClass.simpleName, "FailedAuthenticated")
                }

                override fun invokedPinPassword() {
                    super.invokedPinPassword()
                    Log.d(this@RecoveryAll.javaClass.simpleName, "InvokedPinPassword")
                }
            }

            startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
                putExtra("OtherTitle", getString(R.string.recover_all))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())

        } else {

            startService(Intent(applicationContext, RecoveryShortcuts::class.java))
            startService(Intent(applicationContext, RecoveryFolders::class.java))
            startService(Intent(applicationContext, RecoveryWidgets::class.java))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopForeground(true)
            }

            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(333, functionsClassLegacy.bindServiceNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(333, functionsClassLegacy.bindServiceNotification())
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopForeground(true)
        }

        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {

        return null
    }
}