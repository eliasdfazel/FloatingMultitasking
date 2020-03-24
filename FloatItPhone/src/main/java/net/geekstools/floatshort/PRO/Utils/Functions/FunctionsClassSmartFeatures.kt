/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.app.usage.UsageStatsManager
import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.*

class FunctionsClassSmartFeatures() {

    fun letMeKnow(context: Context, maxValue: Int, startTime: Long /*‪86400000‬ = 1 days*/, endTime: Long /*System.currentTimeMillis()*/) : Deferred<List<String>> = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {
        /*‪86400000 = 24h --- 82800000 = 23h‬*/
        val frequentlyUsedApps: ArrayList<String> = ArrayList()

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val queryUsageStats = usageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                        System.currentTimeMillis() - startTime,
                        endTime)
        queryUsageStats.sortWith(Comparator {
            left, right -> right.totalTimeInForeground.compareTo(left.totalTimeInForeground)
        })

        val functionsClassApplicationsData = FunctionsClassApplicationsData(FunctionsClassDataContext(context))

        queryUsageStats.asFlow()
                .map {
                    val packageNameUsedApplication = it.packageName

                    packageNameUsedApplication
                }
                .filter { packageNameUsedApplication ->

                    functionsClassApplicationsData.appIsInstalled(packageNameUsedApplication)
                            && !functionsClassApplicationsData.isSystemApplication(packageNameUsedApplication)
                            && !functionsClassApplicationsData.isDefaultLauncher(packageNameUsedApplication)
                            && functionsClassApplicationsData.canLaunch(packageNameUsedApplication)
                }
                .collect {
                    frequentlyUsedApps.add(it)
                }

        frequentlyUsedApps.distinct()
    }
}