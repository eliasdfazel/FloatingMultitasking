/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:57 AM
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
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import androidx.preference.PreferenceManager
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

class RecoveryFolders : Service() {

    private val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }
    private val fileIO: FileIO by lazy {
        FileIO(applicationContext)
    }
    private val floatingServices: FloatingServices by lazy {
        FloatingServices(applicationContext)
    }

    var permitOpenFloatingFolder: Boolean = true

    override fun onBind(intent: Intent): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        PublicVariable.floatingSizeNumber = functionsClassLegacy.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

        if (!applicationContext.getFileStreamPath(".uCategory").exists()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopForeground(true)
            }

            this@RecoveryFolders.stopSelf()

        } else {

            intent?.let {

                val foldersDataLines = fileIO.readFileLinesAsArray(".uCategory")

                if (!foldersDataLines.isNullOrEmpty()) {

                    val authenticatedFloatIt = intent.getBooleanExtra("AuthenticatedFloatIt", false)

                    if (functionsClassLegacy.securityServicesSubscribed()
                            && !authenticatedFloatIt) {

                        SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                            override fun authenticatedFloatIt() {
                                super.authenticatedFloatIt()
                                Log.d(this@RecoveryFolders.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                                floatingFoldersRecoveryProcess(foldersDataLines)
                            }

                            override fun failedAuthenticated() {
                                super.failedAuthenticated()
                                Log.d(this@RecoveryFolders.javaClass.simpleName, "FailedAuthenticated")

                                this@RecoveryFolders.stopSelf()
                            }

                            override fun invokedPinPassword() {
                                super.invokedPinPassword()
                                Log.d(this@RecoveryFolders.javaClass.simpleName, "InvokedPinPassword")
                            }
                        }

                        startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
                            putExtra(UserInterfaceExtraData.OtherTitle, getString(R.string.floatingFolders))
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())

                    } else {

                        floatingFoldersRecoveryProcess(foldersDataLines)
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
            startForeground(333, functionsClassLegacy.bindServiceNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(333, functionsClassLegacy.bindServiceNotification())
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
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopForeground(true)
        }

        this@RecoveryFolders.stopSelf()
    }

    private fun floatingFoldersRecoveryProcess(foldersDataLines: Array<String>) {

        if (functionsClassLegacy.customIconsEnable()) {
            val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClassLegacy.customIconPackageName())
            loadCustomIcons.load()
        }

        for (aFolderLine in foldersDataLines) {
            permitOpenFloatingFolder = true

            if (PublicVariable.floatingFoldersList != null) {

                for (check in PublicVariable.floatingFoldersList.indices) {

                    if (aFolderLine == PublicVariable.floatingFoldersList[check]) {
                        permitOpenFloatingFolder = false
                    }
                }
            }

            if (permitOpenFloatingFolder) {
                floatingServices.runUnlimitedFoldersService(aFolderLine)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopForeground(true)
        }

        this@RecoveryFolders.stopSelf()
    }
}