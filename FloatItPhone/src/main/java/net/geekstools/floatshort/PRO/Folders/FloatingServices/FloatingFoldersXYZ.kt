package net.geekstools.floatshort.PRO.Folders.FloatingServices

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder

class FloatingFoldersXYZ : Service() {

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