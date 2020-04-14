package net.geekstools.floatshort.PRO.Folders.Extensions

import android.content.pm.ApplicationInfo
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.*
import net.geekstools.floatshort.PRO.Folders.AppSelectionList
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.AppSelectionListAdapter
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.PopupIndexedFastScroller.IndexedFastScroller
import java.util.*
import kotlin.collections.ArrayList

fun AppSelectionList.loadInstalledAppsData() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
    installedAppsListItem.clear()

    val listOfNewCharOfItemsForIndex: ArrayList<String> = ArrayList<String>()

    applicationInfoList = applicationContext.packageManager.getInstalledApplications(0) as ArrayList<ApplicationInfo>
    Collections.sort(applicationInfoList, ApplicationInfo.DisplayNameComparator(packageManager))

    applicationInfoList.forEach { applicationInfo ->

        if (applicationContext.packageManager.getLaunchIntentForPackage((applicationInfo).packageName) != null) {

            val packageName = applicationInfo.packageName
            val appName = functionsClass.appName(packageName)
            val appIcon = if (functionsClass.customIconsEnable()) {
                loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.shapedAppIcon(packageName))
            } else {
                functionsClass.shapedAppIcon(packageName)
            }

            listOfNewCharOfItemsForIndex.add(appName.substring(0, 1).toUpperCase(Locale.getDefault()))
            installedAppsListItem.add(AdapterItems(appName, packageName, appIcon))
        }
    }

    withContext(Dispatchers.Main) {

        appSelectionListAdapter = AppSelectionListAdapter(applicationContext,
                functionsClass,
                advanceAppSelectionListBinding,
                installedAppsListItem,
                appsConfirmButton,
                this@loadInstalledAppsData)

        advanceAppSelectionListBinding.recyclerListView.adapter = appSelectionListAdapter

        advanceAppSelectionListBinding.folderNameView.visibility = View.VISIBLE

        advanceAppSelectionListBinding.loadingSplash.visibility = View.INVISIBLE

        if (!resetAdapter) {
            val animationFadeOut = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            advanceAppSelectionListBinding.loadingSplash.startAnimation(animationFadeOut)
        }

        appsConfirmButton?.makeItVisible()

        val animationFadeIn = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in)
        advanceAppSelectionListBinding.appSelectedCounterView.startAnimation(animationFadeIn)
        animationFadeIn.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

                advanceAppSelectionListBinding.appSelectedCounterView.text = functionsClass.countLineInnerFile(PublicVariable.categoryName).toString()
            }

            override fun onAnimationEnd(animation: Animation) {

                advanceAppSelectionListBinding.appSelectedCounterView.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        resetAdapter = false
    }

    /*Indexed Popup Fast Scroller*/
    val indexedFastScroller: IndexedFastScroller = IndexedFastScroller(
            context = applicationContext,
            layoutInflater = layoutInflater,
            rootView = advanceAppSelectionListBinding.MainView,
            nestedScrollView = advanceAppSelectionListBinding.nestedScrollView,
            recyclerView = advanceAppSelectionListBinding.recyclerListView,
            fastScrollerIndexViewBinding = advanceAppSelectionListBinding.fastScrollerIndexInclude
    )
    indexedFastScroller.popupEnable = !functionsClass.litePreferencesEnabled()
    indexedFastScroller.initializeIndexView(0,
            0,
            0,
            0
    ).loadIndexData(
            listOfNewCharOfItemsForIndex = listOfNewCharOfItemsForIndex
    ).await()
    /*Indexed Popup Fast Scroller*/
}