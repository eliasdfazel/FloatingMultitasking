package net.geekstools.floatshort.PRO.Folders.Extensions

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import kotlinx.coroutines.*
import net.geekstools.floatshort.PRO.Folders.AppSelectionList
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.AppSelectionListAdapter
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import java.util.*

fun AppSelectionList.loadInstalledAppsData() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
    installedAppsListItem.clear()
    advanceAppSelectionListBinding.indexView.removeAllViews()

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

            override fun onAnimationRepeat(animation: Animation) {}
        })

        resetAdapter = false

        LoadApplicationsIndex().await()
    }
}

fun AppSelectionList.LoadApplicationsIndex() = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {
    advanceAppSelectionListBinding.indexView.removeAllViews()

    withContext(Dispatchers.Default) {
        for (itemCount in installedAppsListItem.indices) {
            val index = installedAppsListItem[itemCount].appName.substring(0, 1).toUpperCase(Locale.getDefault())

            if (mapIndexFirstItem[index] == null) {
                mapIndexFirstItem[index] = itemCount
            }

            mapIndexLastItem[index] = itemCount
        }
    }

    var sideIndexItem = layoutInflater.inflate(R.layout.side_index_item, null) as TextView
    val indexList: List<String> = ArrayList(mapIndexFirstItem.keys)

    for (index in indexList) {
        sideIndexItem = layoutInflater.inflate(R.layout.side_index_item, null) as TextView
        sideIndexItem.text = index.toUpperCase(Locale.getDefault())
        sideIndexItem.setTextColor(PublicVariable.colorLightDarkOpposite)

        advanceAppSelectionListBinding.indexView.addView(sideIndexItem)
    }

    val finalTextView = sideIndexItem

    delay(777)

    var upperRange = (advanceAppSelectionListBinding.indexView.y - finalTextView.height).toInt()

    for (number in 0 until advanceAppSelectionListBinding.indexView.childCount) {
        val indexText = (advanceAppSelectionListBinding.indexView.getChildAt(number) as TextView).text.toString()
        val indexRange = (advanceAppSelectionListBinding.indexView.getChildAt(number).y + advanceAppSelectionListBinding.indexView.y + finalTextView.height).toInt()

        for (jRange in upperRange..indexRange) {
            mapRangeIndex[jRange] = indexText
        }

        upperRange = indexRange
    }

    setupFastScrollingIndexing()
}

@SuppressLint("ClickableViewAccessibility")
fun AppSelectionList.setupFastScrollingIndexing() {
    val popupIndexBackground = getDrawable(R.drawable.ic_launcher_balloon)?.mutate()
    popupIndexBackground?.setTint(PublicVariable.primaryColorOpposite)
    advanceAppSelectionListBinding.popupIndex.background = popupIndexBackground

    advanceAppSelectionListBinding.nestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
    advanceAppSelectionListBinding.nestedIndexScrollView.visibility = View.VISIBLE

    val popupIndexOffsetY = (PublicVariable.statusBarHeight + PublicVariable.actionBarHeight + functionsClass.DpToInteger(7)).toFloat()
    advanceAppSelectionListBinding.nestedIndexScrollView.setOnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                if (functionsClass.litePreferencesEnabled()) {

                } else {
                    val indexText = mapRangeIndex[motionEvent.y.toInt()]

                    if (indexText != null) {
                        advanceAppSelectionListBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                        advanceAppSelectionListBinding.popupIndex.text = indexText
                        advanceAppSelectionListBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                        advanceAppSelectionListBinding.popupIndex.visibility = View.VISIBLE
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (functionsClass.litePreferencesEnabled()) {

                } else {
                    val indexText = mapRangeIndex[motionEvent.y.toInt()]

                    if (indexText != null) {
                        if (!advanceAppSelectionListBinding.popupIndex.isShown) {
                            advanceAppSelectionListBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                            advanceAppSelectionListBinding.popupIndex.visibility = View.VISIBLE
                        }
                        advanceAppSelectionListBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                        advanceAppSelectionListBinding.popupIndex.text = indexText
                        try {
                            advanceAppSelectionListBinding.nestedScrollView.smoothScrollTo(
                                    0,
                                    advanceAppSelectionListBinding.recyclerListView.getChildAt(mapIndexFirstItem[mapRangeIndex[motionEvent.y.toInt()]]!!).y.toInt()
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        if (advanceAppSelectionListBinding.popupIndex.isShown) {
                            advanceAppSelectionListBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                            advanceAppSelectionListBinding.popupIndex.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (functionsClass.litePreferencesEnabled()) {
                    try {
                        advanceAppSelectionListBinding.nestedScrollView.smoothScrollTo(
                                0,
                                advanceAppSelectionListBinding.recyclerListView.getChildAt(mapIndexFirstItem[mapRangeIndex[motionEvent.y.toInt()]]!!).y.toInt()
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    if (advanceAppSelectionListBinding.popupIndex.isShown) {
                        try {
                            advanceAppSelectionListBinding.nestedScrollView.smoothScrollTo(
                                    0,
                                    advanceAppSelectionListBinding.recyclerListView.getChildAt(mapIndexFirstItem[mapRangeIndex[motionEvent.y.toInt()]]!!).y.toInt()
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        advanceAppSelectionListBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                        advanceAppSelectionListBinding.popupIndex.visibility = View.INVISIBLE
                    }
                }
            }
        }
        true
    }
}