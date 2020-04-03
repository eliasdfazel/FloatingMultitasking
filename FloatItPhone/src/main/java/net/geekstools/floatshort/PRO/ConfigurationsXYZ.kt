package net.geekstools.floatshort.PRO

import android.app.usage.UsageStats
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class ConfigurationsXYZ : AppCompatActivity() {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(applicationContext)
    }

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)

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

        if (getFileStreamPath(".uFile").exists()) {

            functionsClass.readFileLine(".uFile").forEach {

                functionsClass.IndexAppInfoShortcuts(
                        functionsClass.appName(it) + " | " + it
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        functionsClass.endIndexAppInfo()
    }

    @Throws(Exception::class)
    fun retrieveFreqUsedApp(functionsClass: FunctionsClass, arraySize: Int): Array<String>? {
        val freqApps: List<String> = functionsClass.letMeKnow(this@ConfigurationsXYZ, 25, (86400000 * 7).toLong(), System.currentTimeMillis())

        return freqApps.toTypedArray()
    }

    private class LastTimeLaunchedComparator : Comparator<UsageStats> {

        override fun compare(usageStatsLeft: UsageStats, usageStatsRight: UsageStats): Int {

            return usageStatsRight.lastTimeUsed.compareTo(usageStatsLeft.lastTimeUsed)
        }
    }
}