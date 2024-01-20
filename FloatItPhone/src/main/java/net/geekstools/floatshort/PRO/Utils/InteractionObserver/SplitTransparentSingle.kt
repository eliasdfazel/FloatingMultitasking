/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.InteractionObserver

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchResultType
import net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter.SplitAdapter
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsApplications
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.SplitItBinding

@Suppress("DEPRECATION")
class SplitTransparentSingle : Activity() {

    private val functionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    private val applicationsViewItemsAdapter by lazy {
        SplitAdapter(this@SplitTransparentSingle, allApplications)
    }

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClassLegacy.customIconPackageName())
    }

    private val allApplications = ArrayList<AdapterItemsApplications>()

    lateinit var splitItBinding: SplitItBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splitItBinding = SplitItBinding.inflate(layoutInflater)
        setContentView(splitItBinding.root)

        window.statusBarColor = getColor(R.color.dark)
        window.navigationBarColor = getColor(R.color.dark)

        val gridLayoutManager = GridLayoutManager(applicationContext, functionsClassLegacy.columnCount(105))
        splitItBinding.applicationsGirdView.layoutManager = gridLayoutManager

        splitItBinding.applicationsGirdView.adapter = applicationsViewItemsAdapter

        val accessibilityManager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager

        val accessibilityEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AccessibilityEvent()
        } else {
            AccessibilityEvent.obtain()
        }
        accessibilityEvent.setSource(Button(applicationContext))
        accessibilityEvent.eventType = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        accessibilityEvent.action = 69201
        accessibilityEvent.className = SplitTransparentPair::class.java.simpleName
        accessibilityEvent.text.add(packageName)

        accessibilityManager.sendAccessibilityEvent(accessibilityEvent)

        var splitSingle: Intent? = Intent()
        if (PublicVariable.splitSingleClassName != null) {
            splitSingle?.setClassName(PublicVariable.splitSinglePackage, PublicVariable.splitSingleClassName)
        } else {
            splitSingle = packageManager.getLaunchIntentForPackage(PublicVariable.splitSinglePackage)
        }
        splitSingle?.flags = Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(splitSingle)

        suggestApplications()

    }

    fun suggestApplications() = CoroutineScope(Dispatchers.IO + SupervisorJob()).async {

        if (functionsClassLegacy.customIconsEnable()) {
            loadCustomIcons.load()
        }

        val applicationInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            packageManager.queryIntentActivities(Intent().apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
            }, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong()))

        } else {

            packageManager.queryIntentActivities(Intent().apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
            }, PackageManager.MATCH_ALL)

        }

        val applicationInfoListSorted = applicationInfoList.sortedWith(ResolveInfo.DisplayNameComparator(packageManager))

        delay(666)

        applicationInfoListSorted.forEach {

            allApplications.add(AdapterItemsApplications(it.loadLabel(packageManager).toString(),
                it.activityInfo.packageName,
                it.activityInfo.name,
                if (functionsClassLegacy.customIconsEnable()) {
                    loadCustomIcons.getDrawableIconForPackage(it.activityInfo.packageName, functionsClassLegacy.shapedAppIcon(it.activityInfo))
                } else {
                    functionsClassLegacy.shapedAppIcon(it.activityInfo)
                },
                functionsClassLegacy.extractDominantColor(it.activityInfo.loadIcon(packageManager)),
                SearchResultType.SearchShortcuts))

        }

        withContext(Dispatchers.Main) {

            applicationsViewItemsAdapter.notifyDataSetChanged()

        }

    }

}
