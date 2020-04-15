/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 4/15/20 12:41 AM
 * Last modified 4/15/20 12:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FloatingServices

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class FloatingFoldersXYZ : Service() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }
}