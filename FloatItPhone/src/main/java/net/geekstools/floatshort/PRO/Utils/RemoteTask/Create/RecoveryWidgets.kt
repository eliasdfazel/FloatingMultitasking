/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/28/20 4:56 PM
 * Last modified 3/28/20 4:53 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.app.ActivityOptions
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassSecurity
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataModel
import net.geekstools.floatshort.PRO.Widget.WidgetsReallocationProcess

class RecoveryWidgets : Service() {

    lateinit var functionsClass: FunctionsClass
    lateinit var functionsClassSecurity: FunctionsClassSecurity

    var noRecovery = false

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
            stopSelf()
            return START_NOT_STICKY
        }
        if (functionsClass.securityServicesSubscribed() && !FunctionsClassSecurity.alreadyAuthenticating) {
            FunctionsClassSecurity.authComponentName = packageName
            FunctionsClassSecurity.authSecondComponentName = packageName
            FunctionsClassSecurity.authRecovery = true

            functionsClassSecurity.openAuthInvocation()
            FunctionsClassSecurity.alreadyAuthenticating = true

            val intentFilter = IntentFilter()
            intentFilter.addAction("RECOVERY_AUTHENTICATED")
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == "RECOVERY_AUTHENTICATED") {

                        CoroutineScope(Dispatchers.IO).launch {

                            try {
                                if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {

                                    if (functionsClass.loadCustomIcons()) {
                                        val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
                                        loadCustomIcons.load()
                                        println("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIconsNumber())
                                    }

                                    val widgetDataInterface: WidgetDataInterface = Room.databaseBuilder(applicationContext, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                            .fallbackToDestructiveMigration()
                                            .addCallback(object : RoomDatabase.Callback() {
                                                override fun onCreate(sqLiteDatabase: SupportSQLiteDatabase) {
                                                    super.onCreate(sqLiteDatabase)
                                                }

                                                override fun onOpen(sqLiteDatabase: SupportSQLiteDatabase) {
                                                    super.onOpen(sqLiteDatabase)
                                                }
                                            })
                                            .build()
                                    val widgetDataModels: List<WidgetDataModel> = widgetDataInterface.initDataAccessObject().getAllWidgetDataSuspend()

                                    AllWidgetData@ for (widgetDataModel in widgetDataModels) {
                                        if (widgetDataModel.Recovery!!) {
                                            noRecovery = false

                                            FloatingWidgetCheck@ for (floatingWidgetCheck in PublicVariable.FloatingWidgets) {
                                                if (widgetDataModel.WidgetId == floatingWidgetCheck) {
                                                    continue@AllWidgetData
                                                }
                                            }

                                            try {
                                                functionsClass.runUnlimitedWidgetService(widgetDataModel.WidgetId, widgetDataModel.WidgetLabel)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        } else {
                                            noRecovery = true
                                        }
                                    }

                                    widgetDataInterface.close()

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        stopForeground(Service.STOP_FOREGROUND_REMOVE)
                                        stopForeground(true)
                                    }
                                    stopSelf()
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        stopForeground(Service.STOP_FOREGROUND_REMOVE)
                                        stopForeground(true)
                                    }
                                    stopSelf()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    stopForeground(Service.STOP_FOREGROUND_REMOVE)
                                    stopForeground(true)
                                }
                                stopSelf()
                            } finally {
                                if (PublicVariable.floatingCounter == 0) {

                                    if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                                        stopService(Intent(applicationContext, BindServices::class.java))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            try {
                registerReceiver(broadcastReceiver, intentFilter)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            CoroutineScope(Dispatchers.IO).launch {

                try {
                    if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {

                        if (functionsClass.loadCustomIcons()) {
                            val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
                            loadCustomIcons.load()
                        }

                        val widgetDataInterface: WidgetDataInterface = Room.databaseBuilder(applicationContext, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                .fallbackToDestructiveMigration()
                                .addCallback(object : RoomDatabase.Callback() {
                                    override fun onCreate(sqLiteDatabase: SupportSQLiteDatabase) {
                                        super.onCreate(sqLiteDatabase)
                                    }

                                    override fun onOpen(sqLiteDatabase: SupportSQLiteDatabase) {
                                        super.onOpen(sqLiteDatabase)
                                    }
                                })
                                .build()
                        val widgetDataModels: List<WidgetDataModel> = widgetDataInterface.initDataAccessObject().getAllWidgetDataSuspend()

                        AllWidgetData@ for (widgetDataModel in widgetDataModels) {
                            if (widgetDataModel.Recovery!!) {
                                noRecovery = false

                                FloatingWidgetCheck@ for (floatingWidgetCheck in PublicVariable.FloatingWidgets) {
                                    if (widgetDataModel.WidgetId == floatingWidgetCheck) {
                                        continue@AllWidgetData
                                    }
                                }

                                try {
                                    functionsClass.runUnlimitedWidgetService(widgetDataModel.WidgetId, widgetDataModel.WidgetLabel)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                noRecovery = true
                            }
                        }

                        widgetDataInterface.close()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            stopForeground(Service.STOP_FOREGROUND_REMOVE)
                            stopForeground(true)
                        }
                        stopSelf()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            stopForeground(Service.STOP_FOREGROUND_REMOVE)
                            stopForeground(true)
                        }
                        stopSelf()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        stopForeground(Service.STOP_FOREGROUND_REMOVE)
                        stopForeground(true)
                    }
                    stopSelf()
                } finally {
                    if (PublicVariable.floatingCounter == 0) {
                        if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                            stopService(Intent(applicationContext, BindServices::class.java))
                        }
                    }
                }
            }
        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        functionsClass = FunctionsClass(applicationContext)
        functionsClassSecurity = FunctionsClassSecurity(applicationContext)

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
}