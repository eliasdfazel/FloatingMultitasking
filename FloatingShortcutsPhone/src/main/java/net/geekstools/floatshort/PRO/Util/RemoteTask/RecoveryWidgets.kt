package net.geekstools.floatshort.PRO.Util.RemoteTask

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import net.geeksempire.chat.vicinity.Util.RoomSqLiteDatabase.UserInformation.WidgetDataInterface
import net.geeksempire.chat.vicinity.Util.RoomSqLiteDatabase.UserInformation.WidgetDataModel
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons

class RecoveryWidgets : Service() {

    lateinit var functionsClass: FunctionsClass

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Thread(Runnable {
            try {
                if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {

                    if (functionsClass.loadCustomIcons()) {
                        val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
                        loadCustomIcons.load()
                        if (BuildConfig.DEBUG) {
                            println("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons())
                        }
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
                    val widgetDataModels: List<WidgetDataModel> = widgetDataInterface.initDataAccessObject().getAllWidgetData()

                    AllWidgetData@ for (widgetDataModel in widgetDataModels) {
                        if (widgetDataModel.Recovery) {
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
                        }
                    }
                    widgetDataInterface.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (PublicVariable.floatingCounter == 0) {
                    if (PreferenceManager.getDefaultSharedPreferences(applicationContext)
                                    .getBoolean("stable", true) == false) {
                        stopService(Intent(applicationContext, BindServices::class.java))
                    }
                }
            }
        }).start()

        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        functionsClass = FunctionsClass(applicationContext)
    }
}