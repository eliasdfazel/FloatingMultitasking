/*
 * Copyright © 2022 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/19/22, 3:40 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.RemoteTask

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.support.wearable.complications.ComplicationText
import android.util.Log
import net.geekstools.floatshort.PRO.Automation.RecoveryShortcuts
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class RecoveryComplication : ComplicationProviderService() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    companion object {
        private const val ComplicationDataRequestCode = 666
    }

    override fun onComplicationActivated(complicationId: Int, dataType: Int, complicationManager: ComplicationManager) {
        Log.d(this@RecoveryComplication.javaClass.simpleName, "Complication Activated $dataType")

        functionsClass.savePreference("ComplicationProviderService", "ComplicationedId", complicationId)
    }

    override fun onComplicationUpdate(complicationId: Int, type: Int, complicationManager: ComplicationManager) {
        if (type == ComplicationData.TYPE_LARGE_IMAGE || type == ComplicationData.TYPE_ICON || type == ComplicationData.TYPE_RANGED_VALUE) {

            functionsClass.savePreference("ComplicationProviderService", "ComplicationedId", complicationId)

            val recoveryIntent = Intent(applicationContext, RecoveryShortcuts::class.java)
            recoveryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val complicationTogglePendingIntent = PendingIntent
                    .getService(applicationContext, ComplicationDataRequestCode, recoveryIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val complicationData: ComplicationData = when (type) {
                ComplicationData.TYPE_LARGE_IMAGE -> {

                    ComplicationData.Builder(ComplicationData.TYPE_LARGE_IMAGE)
                            .setLargeImage(Icon.createWithResource(this, R.drawable.draw_recovery))
                            .setTapAction(complicationTogglePendingIntent)
                            .build()
                }
                ComplicationData.TYPE_ICON -> {

                    ComplicationData.Builder(ComplicationData.TYPE_ICON)
                        .setIcon(Icon.createWithResource(applicationContext, R.drawable.w_recovery_indicator))
                        .setTapAction(complicationTogglePendingIntent)
                        .build()
                }
                ComplicationData.TYPE_RANGED_VALUE -> {

                    ComplicationData.Builder(ComplicationData.TYPE_RANGED_VALUE)
                        .setIcon(Icon.createWithResource(applicationContext, R.drawable.ic_notification))
                        .setShortText(ComplicationText.plainText(getString(R.string.recoveryEmoji)))
                        .setMinValue(0f)
                        .setMaxValue(functionsClass!!.readPreference("InstalledApps", "countApps", packageManager.getInstalledApplications(0).size).toFloat())
                        .setValue(functionsClass!!.countLine(".uFile").toFloat())
                        .setTapAction(complicationTogglePendingIntent)
                        .build()
                }
                else -> {

                    ComplicationData.Builder(ComplicationData.TYPE_ICON)
                        .setIcon(Icon.createWithResource(applicationContext, R.drawable.w_recovery_indicator))
                        .setTapAction(complicationTogglePendingIntent)
                        .build()
                }
            }
            complicationManager.updateComplicationData(complicationId, complicationData)
        }
    }

    override fun onComplicationDeactivated(complicationId: Int) {
        Log.d(this@RecoveryComplication.javaClass.simpleName, "Complication Deactivated ")
    }
}