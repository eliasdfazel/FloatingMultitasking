/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 10/5/21, 8:13 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.app.ActivityOptions
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import androidx.preference.PreferenceManager
import com.google.firebase.appindexing.FirebaseAppIndex
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.UserInterfaceExtraData
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons

class RecoveryShortcuts : Service() {
    
    private val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    private val fileIO: FileIO by lazy {
        FileIO(applicationContext)
    }
    
    private val floatingServices: FloatingServices by lazy {
        FloatingServices(applicationContext)
    }
    
    private var permitOpenFloatingShortcuts = true

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        PublicVariable.floatingSizeNumber = functionsClassLegacy.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

        if (!applicationContext.getFileStreamPath(".uFile").exists()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopForeground(true)
            }

            this@RecoveryShortcuts.stopSelf()

        } else {

            intent?.let {

                val applicationsDataLines = fileIO.readFileLinesAsArray(".uFile")

                if (!applicationsDataLines.isNullOrEmpty()) {

                    val authenticatedFloatIt = intent.getBooleanExtra("AuthenticatedFloatIt", false)

                    if (functionsClassLegacy.securityServicesSubscribed()
                            && !authenticatedFloatIt) {

                        SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                            override fun authenticatedFloatIt(extraInformation: Bundle?) {
                                super.authenticatedFloatIt(extraInformation)
                                Log.d(this@RecoveryShortcuts.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                                floatingShortcutsRecoveryProcess(applicationsDataLines)
                            }

                            override fun failedAuthenticated() {
                                super.failedAuthenticated()
                                Log.d(this@RecoveryShortcuts.javaClass.simpleName, "FailedAuthenticated")

                                this@RecoveryShortcuts.stopSelf()
                            }

                            override fun invokedPinPassword() {
                                super.invokedPinPassword()
                                Log.d(this@RecoveryShortcuts.javaClass.simpleName, "InvokedPinPassword")
                            }
                        }

                        startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
                            putExtra(UserInterfaceExtraData.OtherTitle, getString(R.string.floatingFolders))
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())

                    } else {

                        floatingShortcutsRecoveryProcess(applicationsDataLines)
                    }
                }

                if (PublicVariable.allFloatingCounter == 0) {
                    if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                        stopService(Intent(applicationContext, BindServices::class.java))
                    }
                }
            }
        }

        if (PublicVariable.allFloatingCounter == 0) {
            if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("stable", true)) {

                stopService(Intent(applicationContext, BindServices::class.java))
            }
        }

        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(333, functionsClassLegacy.bindServiceNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(333, functionsClassLegacy.bindServiceNotification())
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopForeground(true)
        }

        this@RecoveryShortcuts.stopSelf()
    }

    private fun floatingShortcutsRecoveryProcess(applicationsDataLines: Array<String>) {

        FirebaseAppIndex.getInstance(applicationContext).removeAll()

        if (functionsClassLegacy.customIconsEnable()) {
            val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClassLegacy.customIconPackageName())
            loadCustomIcons.load()
        }

        for (applicationsData in applicationsDataLines) {
            permitOpenFloatingShortcuts = true

            if (PublicVariable.floatingShortcutsList != null) {

                for (check in PublicVariable.floatingShortcutsList.indices) {

                    if (applicationsData == PublicVariable.floatingShortcutsList[check]) {
                        permitOpenFloatingShortcuts = false
                    }
                }
            }

            if (permitOpenFloatingShortcuts) {
                floatingServices.runUnlimitedShortcutsServicePackage(applicationsData)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopForeground(true)
        }

        this@RecoveryShortcuts.stopSelf()
    }
}