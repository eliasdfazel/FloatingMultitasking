package net.geekstools.floatshort.PRO.Folders.Extensions

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.*
import net.geekstools.floatshort.PRO.Folders.AppSelectionList
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.AppSelectionListAdapter
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import java.util.*

fun AppSelectionList.loadInstalledAppsData() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
    advanceAppSelectionListBinding.indexView.removeAllViews()

    val installedAppsListItem: ArrayList<AdapterItems> = ArrayList<AdapterItems>()
    installedAppsListItem.clear()

    applicationInfoList = applicationContext.packageManager.getInstalledApplications(0) as ArrayList<ApplicationInfo>
    Collections.sort(applicationInfoList, ApplicationInfo.DisplayNameComparator(packageManager))

    applicationInfoList.forEach { applicationInfo ->

        if (applicationContext.packageManager.getLaunchIntentForPackage((applicationInfo).packageName) != null) {

            val packageName = applicationInfo.packageName
            val appName = functionsClass.appName(packageName)
            val appIcon = if (functionsClass.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.shapedAppIcon(packageName)) else functionsClass.shapedAppIcon(packageName)

            installedAppsListItem.add(AdapterItems(appName, packageName, appIcon))
        }
    }

    appSelectionListAdapter = AppSelectionListAdapter(applicationContext, advanceAppSelectionListBinding,
            installedAppsListItem,
            this@loadInstalledAppsData)

    advanceAppSelectionListBinding.recyclerListView.adapter = appSelectionListAdapter

    advanceAppSelectionListBinding.folderNameView.visibility = View.VISIBLE

    advanceAppSelectionListBinding.loadingSplash.visibility = View.INVISIBLE

    if (!resetAdapter) {
        val animationFadeOut = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
        advanceAppSelectionListBinding.loadingSplash.startAnimation(animationFadeOut)
    }

    sendBroadcast(Intent(getString(R.string.visibilityActionAdvance)))

    val animationFadeIn = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in)
    advanceAppSelectionListBinding.appSelectedCounterView.startAnimation(animationFadeIn)
    animationFadeIn.setAnimationListener(object : Animation.AnimationListener {

        override fun onAnimationStart(animation: Animation) {

            advanceAppSelectionListBinding.appSelectedCounterView.text = functionsClass.countLineInnerFile(PublicVariable.categoryName).toString()
        }

        override fun onAnimationEnd(animation: Animation) {
            advanceAppSelectionListBinding.appSelectedCounterView.visibility = View.VISIBLE
        }

        override fun onAnimationRepeat(animation: Animation) {}
    })

    resetAdapter = false
    println(">>>>>>>>>>>>>>>>> 2")

    LoadApplicationsIndex().await()
}

fun AppSelectionList.LoadApplicationsIndex() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {

    withContext(Dispatchers.Main) {
        setupFastScrollingIndexing()
    }
}

fun AppSelectionList.setupFastScrollingIndexing() {

}