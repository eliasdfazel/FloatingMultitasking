/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/11/20 10:47 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widgets

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataModel
import net.geekstools.floatshort.PRO.databinding.ReallocatingBinding

class WidgetsReallocationProcess : Activity() {

    lateinit var functionsClassLegacy: FunctionsClassLegacy

    lateinit var appWidgetHost: AppWidgetHost

    lateinit var widgetDataModelsReallocation: List<WidgetDataModel>
    var REALLOCATION_COUNTER: Int = 0
    val WIDGETS_REALLOCATION_REQUEST: Int = 777

    lateinit var reallocatingBinding: ReallocatingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reallocatingBinding = ReallocatingBinding.inflate(layoutInflater)
        setContentView(reallocatingBinding.root)

        functionsClassLegacy = FunctionsClassLegacy(applicationContext)

        window.statusBarColor = PublicVariable.primaryColor
        window.navigationBarColor = PublicVariable.primaryColor

        reallocatingBinding.fullViewAllocating.setBackgroundColor(PublicVariable.primaryColor)
        reallocatingBinding.allocatingProgress.setColor(PublicVariable.primaryColorOpposite)

        if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
            appWidgetHost = AppWidgetHost(applicationContext, System.currentTimeMillis().toInt())

            CoroutineScope(Dispatchers.IO).launch {

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
                widgetDataModelsReallocation = widgetDataInterface.initDataAccessObject().getAllWidgetDataSuspend()

                if (widgetDataModelsReallocation.isNotEmpty()) {

                    withContext(Dispatchers.Main) {

                        if (REALLOCATION_COUNTER < widgetDataModelsReallocation.size) {

                            widgetsReallocationProcess(widgetDataModelsReallocation[REALLOCATION_COUNTER], appWidgetHost)
                        }
                    }
                } else {
                    this@WidgetsReallocationProcess.finish()
                }

                widgetDataInterface.close()
            }

            reallocatingBinding.widgetInformation.setOnClickListener {
                startActivity(Intent(applicationContext, WidgetsReallocationProcess::class.java),
                        ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
            }
        } else {
            this@WidgetsReallocationProcess.finish()
        }
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
                        functionsClassLegacy.savePreference("WidgetsInformation", "Reallocated", true)

                        startActivity(Intent(applicationContext, WidgetConfigurations::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

                        this@WidgetsReallocationProcess.finish()
                    }
                }
            }
        }
    }

    private fun widgetsReallocationProcess(widgetDataModel: WidgetDataModel, appWidgetHost: AppWidgetHost) = CoroutineScope(Dispatchers.Main).launch {

        if (functionsClassLegacy.appIsInstalled(widgetDataModel.PackageName)) {

            val widgetId = appWidgetHost.allocateAppWidgetId()


            reallocatingBinding.widgetInformation.setBackgroundColor(PublicVariable.colorLightDark)
            reallocatingBinding.widgetInformation.alpha = 0.77F

            val valueAnimatorScaleWidgetInformation =
                    ValueAnimator.ofInt(
                        reallocatingBinding.widgetInformation.width,
                            functionsClassLegacy.DpToInteger(51)
                    )
            valueAnimatorScaleWidgetInformation.duration = 1000
            valueAnimatorScaleWidgetInformation.addUpdateListener { animator ->
                reallocatingBinding.widgetInformation.layoutParams.width = (animator.animatedValue as Int)
                reallocatingBinding.widgetInformation.requestLayout()
                if ((animator.animatedValue as Int) < functionsClassLegacy.DpToInteger(300)) {
                    reallocatingBinding.widgetInformation.text = null
                    reallocatingBinding.widgetInformation.icon = null

                    Handler(Looper.getMainLooper()).postDelayed({

                        reallocatingBinding.widgetInformation.icon = functionsClassLegacy.applicationIcon(widgetDataModel.PackageName)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            reallocatingBinding.widgetInformation.text = Html.fromHtml("<big><b>" + widgetDataModel.AppName + "</b></big><br/>"
                                    + widgetDataModel.WidgetLabel + "<br/>"
                                    + "<small>" + getString(R.string.reallocatingWidgets) + "</small>", Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            reallocatingBinding.widgetInformation.text = Html.fromHtml("<big><b>" + widgetDataModel.AppName + "</b></big><br/>"
                                    + widgetDataModel.WidgetLabel + "<br/>"
                                    + "<small>" + getString(R.string.reallocatingWidgets) + "</small>", Html.FROM_HTML_MODE_COMPACT)
                        }
                    }, 577)
                }
            }
            valueAnimatorScaleWidgetInformation.start()
            valueAnimatorScaleWidgetInformation.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    val valueAnimatorScaleWidgetInformationRevert =
                            ValueAnimator.ofInt(
                                    functionsClassLegacy.DpToInteger(51),
                                    functionsClassLegacy.DpToInteger(313)
                            )
                    valueAnimatorScaleWidgetInformationRevert.duration = 500
                    valueAnimatorScaleWidgetInformationRevert.addUpdateListener { animator ->
                        reallocatingBinding.widgetInformation.layoutParams.width = (animator.animatedValue as Int)
                        reallocatingBinding.widgetInformation.requestLayout()
                    }
                    valueAnimatorScaleWidgetInformationRevert.start()
                    valueAnimatorScaleWidgetInformationRevert.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {

                        }

                        override fun onAnimationCancel(animation: Animator) {

                        }

                        override fun onAnimationStart(animation: Animator) {

                        }
                    })
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationStart(animation: Animator) {

                }
            })

            val provider = ComponentName.createRelative(widgetDataModel.PackageName, widgetDataModel.ClassNameProvider)

            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider)
            startActivityForResult(intent, WIDGETS_REALLOCATION_REQUEST)

            if (widgetDataModel.ConfigClassName != null) {
                val configure = ComponentName.createRelative(widgetDataModel.PackageName, widgetDataModel.ConfigClassName!!)

                val intentWidgetConfiguration = Intent()
                intentWidgetConfiguration.action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
                intentWidgetConfiguration.component = configure
                intentWidgetConfiguration.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                intentWidgetConfiguration.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider)
                intentWidgetConfiguration.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivityForResult(intentWidgetConfiguration, WIDGETS_REALLOCATION_REQUEST)
            }

            withContext(Dispatchers.IO) {

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
                if (widgetDataModel.PackageName.isEmpty() && widgetDataModel.ClassNameProvider.isEmpty()) {

                    widgetDataDAO.deleteSuspend(widgetDataModel)
                } else {

                    widgetDataDAO.updateWidgetIdByPackageNameClassNameSuspend(widgetDataModel.PackageName, widgetDataModel.ClassNameProvider, widgetId)
                }
            }

            REALLOCATION_COUNTER++
        }
    }
}