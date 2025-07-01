/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.Extensions

import android.content.pm.ApplicationInfo
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.geekstools.floatshort.PRO.Folders.FoldersApplicationsSelectionProcess.Adapter.AppSelectionListAdapter
import net.geekstools.floatshort.PRO.Folders.FoldersApplicationsSelectionProcess.AppSelectionList
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.PopupIndexedFastScroller.Factory.IndexedFastScrollerFactory
import net.geekstools.floatshort.PRO.Utils.UI.PopupIndexedFastScroller.IndexedFastScroller
import java.util.Collections
import java.util.Locale

fun AppSelectionList.loadInstalledAppsData() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
    installedAppsListItem.clear()

    val listOfNewCharOfItemsForIndex: ArrayList<String> = ArrayList<String>()

    applicationInfoList = applicationContext.packageManager.getInstalledApplications(0) as ArrayList<ApplicationInfo>
    Collections.sort(applicationInfoList, ApplicationInfo.DisplayNameComparator(packageManager))

    applicationInfoList.forEach { applicationInfo ->

        if (applicationContext.packageManager.getLaunchIntentForPackage((applicationInfo).packageName) != null) {

            val packageName = applicationInfo.packageName
            val appName = functionsClassLegacy.applicationName(packageName)
            val appIcon = if (functionsClassLegacy.customIconsEnable()) {
                loadCustomIcons.getDrawableIconForPackage(packageName, functionsClassLegacy.shapedAppIcon(packageName))
            } else {
                functionsClassLegacy.shapedAppIcon(packageName)
            }

            installedAppsListItem.add(
                    AdapterItems(appName,
                            packageName,
                            appIcon))

            listOfNewCharOfItemsForIndex.add(appName.substring(0, 1).uppercase(Locale.getDefault()))
        }
    }

    withContext(Dispatchers.Main) {

        appSelectionListAdapter = AppSelectionListAdapter(applicationContext,
                functionsClassLegacy,
                advanceAppSelectionListBinding,
                installedAppsListItem,
                appsConfirmButton!!,
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

                advanceAppSelectionListBinding.appSelectedCounterView.text = fileIO.fileLinesCounter(PublicVariable.folderName).toString()
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
            context = this@loadInstalledAppsData,
            layoutInflater = layoutInflater,
            rootView = advanceAppSelectionListBinding.MainView,
            nestedScrollView = advanceAppSelectionListBinding.nestedScrollView,
            recyclerView = advanceAppSelectionListBinding.recyclerListView,
            fastScrollerIndexViewBinding = advanceAppSelectionListBinding.fastScrollerIndexInclude,
            indexedFastScrollerFactory = IndexedFastScrollerFactory(
                popupEnable = !functionsClassLegacy.litePreferencesEnabled(),
                popupBackgroundTint = PublicVariable.primaryColor,
                popupTextColor = PublicVariable.colorLightDark,
                indexItemTextColor = PublicVariable.colorLightDarkOpposite,
                popupVerticalOffset = (77/3).toFloat()
            )
    )
    indexedFastScroller.initializeIndexView().await()
            .loadIndexData(listOfNewCharOfItemsForIndex = listOfNewCharOfItemsForIndex).await()
    /*Indexed Popup Fast Scroller*/
}