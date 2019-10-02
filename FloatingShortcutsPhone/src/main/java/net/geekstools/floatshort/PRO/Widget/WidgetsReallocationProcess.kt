package net.geekstools.floatshort.PRO.Widget

import android.app.Activity
import android.app.ActivityOptions
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.android.synthetic.main.reallocating.*
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataModel

class WidgetsReallocationProcess : Activity() {

    lateinit var functionsClass: FunctionsClass

    lateinit var appWidgetHost: AppWidgetHost

    lateinit var widgetDataModelsReallocation: List<WidgetDataModel>
    var REALLOCATION_COUNTER: Int = 0
    val WIDGETS_REALLOCATION_REQUEST: Int = 777

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reallocating)

        functionsClass = FunctionsClass(applicationContext, this@WidgetsReallocationProcess)

        window.statusBarColor = PublicVariable.primaryColor
        window.navigationBarColor = PublicVariable.primaryColor

        fullViewAllocating.setBackgroundColor(PublicVariable.primaryColor)
        allocatingProgress.setColor(PublicVariable.primaryColorOpposite)

        appWidgetHost = AppWidgetHost(getApplicationContext(), System.currentTimeMillis().toInt())

        Thread(Runnable {
            val widgetDataInterface = Room.databaseBuilder(applicationContext, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                            super.onCreate(supportSQLiteDatabase)
                        }

                        override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                            super.onOpen(supportSQLiteDatabase)

                        }
                    })
                    .build()
            widgetDataModelsReallocation = widgetDataInterface.initDataAccessObject().getAllWidgetData()

            runOnUiThread {
                if (REALLOCATION_COUNTER < widgetDataModelsReallocation.size) {
                    widgetsReallocationProcess(widgetDataModelsReallocation.get(REALLOCATION_COUNTER), appWidgetHost)
                }
            }

            widgetDataInterface.close()
        }).start()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                WIDGETS_REALLOCATION_REQUEST -> {
                    if (REALLOCATION_COUNTER < widgetDataModelsReallocation.size) {
                        widgetsReallocationProcess(widgetDataModelsReallocation[REALLOCATION_COUNTER], appWidgetHost)
                    } else if (REALLOCATION_COUNTER >= widgetDataModelsReallocation.size) {
                        functionsClass.savePreference("WidgetsInformation", "Reallocated", true)

                        startActivity(Intent(applicationContext, WidgetConfigurations::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

                        this@WidgetsReallocationProcess.finish()
                    }
                }
            }
        }
    }

    private fun widgetsReallocationProcess(widgetDataModel: WidgetDataModel, appWidgetHost: AppWidgetHost) {
        val widgetId = appWidgetHost.allocateAppWidgetId()

        runOnUiThread {
            val provider = ComponentName.createRelative(widgetDataModel.PackageName, widgetDataModel.ClassNameProvider)
            FunctionsClassDebug.PrintDebug("*** Provider Widget = $provider")

            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider)
            startActivityForResult(intent, WIDGETS_REALLOCATION_REQUEST)

            if (widgetDataModel.ConfigClassName != null) {
                val configure = ComponentName.createRelative(widgetDataModel.PackageName, widgetDataModel.ConfigClassName!!)
                FunctionsClassDebug.PrintDebug("*** Configure Widget = $configure")

                val intentWidgetConfiguration = Intent()
                intentWidgetConfiguration.action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
                intentWidgetConfiguration.component = configure
                intentWidgetConfiguration.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                intentWidgetConfiguration.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider)
                intentWidgetConfiguration.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivityForResult(intentWidgetConfiguration, WIDGETS_REALLOCATION_REQUEST)
            }
        }

        Thread(Runnable {
            val widgetDataInterface = Room.databaseBuilder(applicationContext, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                            super.onCreate(supportSQLiteDatabase)
                        }

                        override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                            super.onOpen(supportSQLiteDatabase)

                        }
                    })
                    .build()
            val widgetDataDAO = widgetDataInterface.initDataAccessObject()
            widgetDataDAO.updateWidgetIdByPackageNameClassName(widgetDataModel.PackageName, widgetDataModel.ClassNameProvider, widgetId)
        }).start()

        REALLOCATION_COUNTER++
    }
}