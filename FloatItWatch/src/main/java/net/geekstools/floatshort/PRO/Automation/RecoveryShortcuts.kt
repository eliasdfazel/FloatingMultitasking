/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 10:49 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Automation

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.TypedValue
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class RecoveryShortcuts : Service() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private var permitOpenFloatingShortcuts = true

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        PublicVariable.floatingSizeNumber = 31
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

        if (!applicationContext.getFileStreamPath(".uFile").exists()) {

            stopForeground(STOP_FOREGROUND_REMOVE)
            stopForeground(true)

            this@RecoveryShortcuts.stopSelf()

        } else {

            intent?.let {

                val applicationsDataLines = functionsClass.readFileLine(".uFile")

                floatingShortcutsRecoveryProcess(applicationsDataLines)

                if (PublicVariable.allFloatingCounter == 0) {
                    if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                        stopService(Intent(applicationContext, BindServices::class.java))
                    }
                }
            }
        }

        if (PublicVariable.allFloatingCounter == 0) {
            if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                stopService(Intent(applicationContext, BindServices::class.java))
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopForeground(true)

        this@RecoveryShortcuts.stopSelf()
    }

    private fun floatingShortcutsRecoveryProcess(applicationsDataLines: Array<String>) {

        for (applicationsData in applicationsDataLines) {
            permitOpenFloatingShortcuts = true

            if (PublicVariable.floatingShortcutsList != null) {

                for (check in PublicVariable.floatingShortcutsList.indices) {

                    if (applicationsData == PublicVariable.floatingShortcutsList[check]) {
                        permitOpenFloatingShortcuts = false
                    }
                }
            }

            if (permitOpenFloatingShortcuts) {
                functionsClass.runUnlimitedShortcutsServiceRecovery(applicationsData)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopForeground(true)
        }

        this@RecoveryShortcuts.stopSelf()
    }
}