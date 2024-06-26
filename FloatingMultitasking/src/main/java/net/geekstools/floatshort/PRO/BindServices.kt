/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/11/20 10:43 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PreferencesIO
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class BindServices : Service() {

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (startId == 1) {

            val preferencesIO = PreferencesIO(applicationContext)

            PublicVariable.floatingSizeNumber = preferencesIO.readDefaultPreference("floatingSize", 39)
            PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), applicationContext.resources.displayMetrics).toInt()

        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        val functionsClass = FunctionsClassLegacy(applicationContext)

        functionsClass.loadSavedColor()
        functionsClass.checkLightDarkTheme()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(333, functionsClass.bindServiceNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(333, functionsClass.bindServiceNotification())
        }

    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }
}