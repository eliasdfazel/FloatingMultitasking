/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 3:43 PM
 * Last modified 3/26/20 3:03 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.UI.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.UI.Extensions.UserInterfaceExtraData
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widgets.WidgetsReallocationProcess

class FloatingWidgetHomeScreenShortcuts : Activity() {

    lateinit var functionsClass: FunctionsClass
    lateinit var securityFunctions: SecurityFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        functionsClass = FunctionsClass(applicationContext)
        securityFunctions = SecurityFunctions(applicationContext)

        if (!functionsClass.readPreference("WidgetsInformation", "Reallocated", true)
                && getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
            startActivity(Intent(applicationContext, WidgetsReallocationProcess::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

            this@FloatingWidgetHomeScreenShortcuts.finish()
            return
        }

        val packageName = intent.getStringExtra("PackageName")
        val providerClassName = intent.getStringExtra("ProviderClassName")
        val widgetLabel = intent.getStringExtra("ShortcutLabel")

        CoroutineScope(Dispatchers.Main).launch {
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

            val widgetDataModelsReallocation = widgetDataInterface.initDataAccessObject().loadWidgetByClassNameProviderWidgetSuspend(packageName, providerClassName)
            val appWidgetId = widgetDataModelsReallocation.WidgetId

            runOnUiThread {
                if (securityFunctions.isAppLocked(packageName + providerClassName)) {

                    SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                        override fun authenticatedFloatIt(extraInformation: Bundle?) {
                            super.authenticatedFloatIt(extraInformation)
                            Log.d(this@FloatingWidgetHomeScreenShortcuts.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                            functionsClass
                                    .runUnlimitedWidgetService(appWidgetId, widgetLabel)


                            this@FloatingWidgetHomeScreenShortcuts.finish()
                        }

                        override fun failedAuthenticated() {
                            super.failedAuthenticated()
                            Log.d(this@FloatingWidgetHomeScreenShortcuts.javaClass.simpleName, "FailedAuthenticated")

                            this@FloatingWidgetHomeScreenShortcuts.finish()
                        }

                        override fun invokedPinPassword() {
                            super.invokedPinPassword()
                            Log.d(this@FloatingWidgetHomeScreenShortcuts.javaClass.simpleName, "InvokedPinPassword")

                        }
                    }

                    startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
                        putExtra(UserInterfaceExtraData.OtherTitle, widgetLabel)
                        putExtra(UserInterfaceExtraData.PrimaryColor, functionsClass.extractVibrantColor(functionsClass.appIcon(packageName)))
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())

                } else {
                    functionsClass.runUnlimitedWidgetService(appWidgetId, widgetLabel)
                }
            }

            widgetDataInterface.close()
        }
    }
}