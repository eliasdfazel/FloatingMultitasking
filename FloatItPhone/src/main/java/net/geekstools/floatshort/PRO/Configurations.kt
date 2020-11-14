/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/14/20 11:09 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Utils.Functions.*
import net.geekstools.floatshort.PRO.Utils.RemoteTask.BootRecovery
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryAll
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryFolders
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryShortcuts
import java.util.*

class Configurations : AppCompatActivity() {

    val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    val fileIO: FileIO by lazy {
        FileIO(applicationContext)
    }

    val preferencesIO: PreferencesIO by lazy {
        PreferencesIO(applicationContext)
    }

    private val systemInformation: SystemInformation by lazy {
        SystemInformation(applicationContext)
    }

    val popupApplicationShortcuts: PopupApplicationShortcuts by lazy {
        PopupApplicationShortcuts(applicationContext)
    }

    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(applicationContext)
    }

    val smartFeatures: SmartFeatures = SmartFeatures()

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)

        systemInformation.checkDeviceInformation()

        checkUserInformation()
        initializeParameterUI()

        if (sharedPreferences.getBoolean("stable", true)) {
            PublicVariable.Stable = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(applicationContext, BindServices::class.java))
            } else {
                startService(Intent(applicationContext, BindServices::class.java))
            }
        } else {
            PublicVariable.Stable = false
        }

        if (Build.VERSION.SDK_INT >= 26) {
            if (!Settings.canDrawOverlays(applicationContext)
                    || !getSharedPreferences(".Configuration", Context.MODE_PRIVATE).getBoolean("Permissions", false)) {

                startActivity(Intent(applicationContext, Checkpoint::class.java))

                finish()
                return
            }
        } else {
            if (!Settings.canDrawOverlays(applicationContext)) {
                startActivity(Intent(applicationContext, Checkpoint::class.java))

                finish()
                return
            }
        }

        if (smartFeatures.usageStatsEnabled(applicationContext)) {

            retrieveFrequentlyUsedApplications()

        } else {
            sharedPreferences.edit().apply {
                this.putBoolean("smart", false)
                this.apply()
            }

            triggerOpenProcess()

            if (getFileStreamPath("Frequently").exists()) {
                val freqDelete = fileIO.readFileLinesAsArray("Frequently")

                freqDelete?.let {
                    for (deleteFreq in freqDelete) {
                        deleteFile(deleteFreq + "Frequently")
                    }
                }

                fileIO.removeLine(".categoryInfo", "Frequently")
                deleteFile("Frequently")
            }
        }

        indexFloatingShortcuts()

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    @Throws(Exception::class)
    fun retrieveFrequentlyUsedApplications() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

        val smartFeatures: SmartFeatures = SmartFeatures()

        smartFeatures.letMeKnow(this@Configurations, 25, (86400000 * 7).toLong(), System.currentTimeMillis(),
                object : SmartFeaturesResult {

                    override fun frequentlyUsedApplicationsReady(frequentlyUsedApplications: List<String>?) {
                        super.frequentlyUsedApplicationsReady(frequentlyUsedApplications)

                        sharedPreferences.edit().apply {
                            this.putBoolean("smart", true)
                            this.apply()
                        }

                        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                        val queryUsageStats = usageStatsManager
                                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                                        System.currentTimeMillis() - 1000 * 60,  //begin
                                        System.currentTimeMillis()) //end
                        Collections.sort(queryUsageStats, LastTimeLaunchedComparator())

                        val previousAppPackageName = queryUsageStats[1].packageName
                        if (previousAppPackageName.contains("com.google.android.googlequicksearchbox")) {

                            val bundleFirebaseAnalytics = Bundle()
                            bundleFirebaseAnalytics.putString("COUNTRY", functionsClassLegacy.countryIso)
                            firebaseAnalytics.logEvent(Debug.REMOTE_TASK_OK_GOOGLE, bundleFirebaseAnalytics)

                            when (sharedPreferences.getString("boot", "1")) {
                                BootRecovery.Mode.NONE -> {

                                    frequentlyUsedApplications?.let {

                                        triggerOpenProcessWithFrequentApps(it.toTypedArray())
                                    }
                                }
                                BootRecovery.Mode.SHORTCUTS -> {

                                    Intent(applicationContext, RecoveryShortcuts::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startService(this@apply)
                                    }
                                }
                                BootRecovery.Mode.FOLDERS -> {

                                    Intent(applicationContext, RecoveryFolders::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startService(this@apply)
                                    }
                                }
                                BootRecovery.Mode.RECOVER_ALL -> {

                                    Intent(applicationContext, RecoveryAll::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startService(this@apply)
                                    }
                                }
                            }

                            this@Configurations.finish()
                            return
                        } else {

                            frequentlyUsedApplications?.let {

                                triggerOpenProcessWithFrequentApps(it.toTypedArray())
                            }
                        }

                    }

                }).await()

    }

    private class LastTimeLaunchedComparator : Comparator<UsageStats> {

        override fun compare(usageStatsLeft: UsageStats, usageStatsRight: UsageStats): Int {

            return usageStatsRight.lastTimeUsed.compareTo(usageStatsLeft.lastTimeUsed)
        }
    }
}