/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/26/20 7:36 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.app.ActivityOptions
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.UserInterfaceExtraData
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataModel
import net.geekstools.floatshort.PRO.Widgets.WidgetsReallocationProcess

class RecoveryWidgets : Service() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    var notAddedToRecovery = false

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!functionsClass.readPreference("WidgetsInformation", "Reallocated", true)
                && getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {

            startActivity(Intent(applicationContext, WidgetsReallocationProcess::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
                stopForeground(true)
            }

            this@RecoveryWidgets.stopSelf()

        } else {

            intent?.let {

                val authenticatedFloatIt: Boolean = (intent.getBooleanExtra("AuthenticatedFloatIt", false))

                if (functionsClass.securityServicesSubscribed()
                        && !authenticatedFloatIt) {

                    SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                        override fun authenticatedFloatIt(extraInformation: Bundle?) {
                            super.authenticatedFloatIt(extraInformation)
                            Log.d(this@RecoveryWidgets.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                            floatingWidgetsRecoveryProcess()
                        }

                        override fun failedAuthenticated() {
                            super.failedAuthenticated()
                            Log.d(this@RecoveryWidgets.javaClass.simpleName, "FailedAuthenticated")

                            this@RecoveryWidgets.stopSelf()
                        }

                        override fun invokedPinPassword() {
                            super.invokedPinPassword()
                            Log.d(this@RecoveryWidgets.javaClass.simpleName, "InvokedPinPassword")
                        }
                    }

                    startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
                        putExtra(UserInterfaceExtraData.OtherTitle, getString(R.string.floatingFolders))
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())

                } else {

                    floatingWidgetsRecoveryProcess()
                }
            }
        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(333, functionsClass.bindServiceNotification(), Service.STOP_FOREGROUND_REMOVE)
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
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
            stopForeground(true)
        }

        stopSelf()
    }

    private fun floatingWidgetsRecoveryProcess() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {

            if (functionsClass.customIconsEnable()) {
                val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
                loadCustomIcons.load()
            }

            val widgetDataInterface: WidgetDataInterface = Room.databaseBuilder(applicationContext, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            val widgetDataModels: List<WidgetDataModel> = widgetDataInterface.initDataAccessObject().getAllWidgetDataSuspend()

            AllWidgetData@ for (widgetDataModel in widgetDataModels) {

                val widgetDataModelRecovery: Boolean? = widgetDataModel.Recovery

                if(widgetDataModelRecovery != null) {
                    if (widgetDataModelRecovery) {
                        notAddedToRecovery = false

                        FloatingWidgetCheck@ for (floatingWidgetCheck in PublicVariable.floatingWidgetsIdList) {

                            if (widgetDataModel.WidgetId == floatingWidgetCheck) {

                                continue@AllWidgetData
                            }
                        }

                        functionsClass.runUnlimitedWidgetService(widgetDataModel.WidgetId, widgetDataModel.WidgetLabel)

                    } else {

                        notAddedToRecovery = true
                    }
                }
            }

            widgetDataInterface.close()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
                stopForeground(true)
            }

            this@RecoveryWidgets.stopSelf()
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
                stopForeground(true)
            }

            this@RecoveryWidgets.stopSelf()
        }
        if (PublicVariable.allFloatingCounter == 0) {
            if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                stopService(Intent(applicationContext, BindServices::class.java))
            }
        }
    }
}