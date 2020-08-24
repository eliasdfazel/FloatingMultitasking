/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 6:17 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation.RecoveryServices

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.PreferencesIO
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class RecoveryNfc : Service() {

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val floatingServices: FloatingServices = FloatingServices(applicationContext)

        val fileIO: FileIO = FileIO(applicationContext)

        val preferencesIO: PreferencesIO = PreferencesIO(applicationContext)

        PublicVariable.floatingSizeNumber = preferencesIO.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

        if (getFileStreamPath(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", "")).exists()
                && getFileStreamPath(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", "")).isFile) {

            val packageNames = fileIO.readFileLinesAsArray(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", ""))
            if (!packageNames.isNullOrEmpty()) {
                for (aPackageName in packageNames) {
                    floatingServices.runUnlimitedShortcutsForNfc(aPackageName)
                }
            }
        }

        if (getFileStreamPath(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", "") + "Category").exists()
                && getFileStreamPath(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", "") + "Category").isFile) {

            val folderNames = fileIO.readFileLinesAsArray(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", "") + "Category")
            if (!folderNames.isNullOrEmpty()) {
                for (folderName in folderNames) {

                    fileIO.readFileLinesAsArray(folderName)?.let {

                        floatingServices.runUnlimitedFoldersForNfc(folderName, it)
                    }
                }
            }
        }

        stopSelf()

        return Service.START_NOT_STICKY
    }
}