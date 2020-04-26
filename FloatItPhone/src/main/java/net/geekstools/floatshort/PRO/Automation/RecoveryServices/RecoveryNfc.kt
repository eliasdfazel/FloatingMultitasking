/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/26/20 5:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation.RecoveryServices

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class RecoveryNfc : Service() {

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val functionsClass: FunctionsClass = FunctionsClass(applicationContext)

        PublicVariable.floatingSizeNumber = functionsClass.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

        if (getFileStreamPath(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", "")).exists()
                && getFileStreamPath(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", "")).isFile) {

            val packageNames = functionsClass.readFileLine(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", ""))
            if (packageNames.isNotEmpty()) {
                for (aPackageName in packageNames) {
                    functionsClass.runUnlimitedBluetooth(aPackageName)
                }
            }
        }

        if (getFileStreamPath(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", "") + "Category").exists()
                && getFileStreamPath(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", "") + "Category").isFile) {

            val folderNames = functionsClass.readFileLine(".auto" + this@RecoveryNfc.javaClass.simpleName.replace("Recovery", "") + "Category")
            if (folderNames.isNotEmpty()) {
                for (CategoryName in folderNames) {
                    functionsClass.runUnlimitedFolderBluetooth(CategoryName, functionsClass.readFileLine(CategoryName))
                }
            }
        }

        stopSelf()

        return Service.START_NOT_STICKY
    }
}