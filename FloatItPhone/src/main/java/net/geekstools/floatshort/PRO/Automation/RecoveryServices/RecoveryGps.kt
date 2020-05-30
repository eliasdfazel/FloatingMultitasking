/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/29/20 7:19 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation.RecoveryServices

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassPreferences
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassRunServices
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class RecoveryGps : Service() {

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val functionsClassRunServices: FunctionsClassRunServices = FunctionsClassRunServices(applicationContext)

        val functionsClassIO: FunctionsClassIO = FunctionsClassIO(applicationContext)

        val functionsClassPreferences: FunctionsClassPreferences = FunctionsClassPreferences(applicationContext)

        PublicVariable.floatingSizeNumber = functionsClassPreferences.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

        if (getFileStreamPath(".auto" + this@RecoveryGps.javaClass.simpleName.replace("Recovery", "")).exists()
                && getFileStreamPath(".auto" + this@RecoveryGps.javaClass.simpleName.replace("Recovery", "")).isFile) {

            val packageNames = functionsClassIO.readFileLinesAsArray(".auto" + this@RecoveryGps.javaClass.simpleName.replace("Recovery", ""))
            if (!packageNames.isNullOrEmpty()) {
                for (aPackageName in packageNames) {
                    functionsClassRunServices.runUnlimitedShortcutsForGps(aPackageName)
                }
            }
        }

        if (getFileStreamPath(".auto" + this@RecoveryGps.javaClass.simpleName.replace("Recovery", "") + "Category").exists()
                && getFileStreamPath(".auto" + this@RecoveryGps.javaClass.simpleName.replace("Recovery", "") + "Category").isFile) {

            val folderNames = functionsClassIO.readFileLinesAsArray(".auto" + this@RecoveryGps.javaClass.simpleName.replace("Recovery", "") + "Category")
            if (!folderNames.isNullOrEmpty()) {
                for (folderName in folderNames) {

                    functionsClassIO.readFileLinesAsArray(folderName)?.let {

                        functionsClassRunServices.runUnlimitedFoldersForGps(folderName, it)
                    }
                }
            }
        }

        stopSelf()

        return Service.START_NOT_STICKY
    }
}