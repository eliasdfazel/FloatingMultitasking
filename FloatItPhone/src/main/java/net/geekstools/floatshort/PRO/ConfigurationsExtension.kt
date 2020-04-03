package net.geekstools.floatshort.PRO

import android.R
import android.content.Intent
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.Shortcuts.ApplicationsView
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

fun Configurations.checkUserInformation() {

    functionsClass.savePreference(".UserInformation", "isBetaTester", functionsClass.appVersionName(packageName).contains("[BETA]"))
    functionsClass.savePreference(".UserInformation", "installedVersionCode", functionsClass.appVersionCode(packageName))
    functionsClass.savePreference(".UserInformation", "installedVersionName", functionsClass.appVersionName(packageName))
    functionsClass.savePreference(".UserInformation", "deviceModel", functionsClass.deviceName)
    functionsClass.savePreference(".UserInformation", "userRegion", functionsClass.countryIso)

    if (functionsClass.appVersionName(packageName).contains("[BETA]")) {
        functionsClass.saveDefaultPreference("JoinedBetaProgrammer", true)
    }
}

fun Configurations.initializeParameterUI() {

    val typedValue = TypedValue()
    if (theme.resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
        PublicVariable.actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    }
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    PublicVariable.statusBarHeight = result
    PublicVariable.navigationBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"))

    functionsClass.extractWallpaperColor()
    functionsClass.loadSavedColor()
    functionsClass.checkLightDarkTheme()

    PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39)
    PublicVariable.HW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size.toFloat(), this.resources.displayMetrics).toInt()
}

fun Configurations.triggerOpenProcess() {

    if (functionsClass.readPreference("OpenMode", "openClassName", ApplicationsView::class.java.simpleName) == FoldersConfigurations::class.java.simpleName) {//Floating Folder

        Intent(applicationContext, FoldersConfigurations::class.java).apply {

            startActivity(this)
        }

    } else {//Floating Shortcuts

        Intent(applicationContext, ApplicationsView::class.java).apply {

            startActivity(this)
        }

        functionsClass.addAppShortcuts()
    }
}

fun Configurations.triggerOpenProcessWithFrequentApps(frequentAppsArray: Array<String>) {
    if (functionsClass.readPreference("OpenMode", "openClassName", ApplicationsView::class.java.simpleName) == FoldersConfigurations::class.java.simpleName) {//Floating Folder

        if (getFileStreamPath("Frequently").exists()) {
            functionsClass.removeLine(".categoryInfo", "Frequently")
            deleteFile("Frequently")
        }

        PublicVariable.frequentlyUsedApps = frequentAppsArray
        PublicVariable.freqLength = frequentAppsArray.size

        for (frequentApp in frequentAppsArray) {
            functionsClass.saveFileAppendLine("Frequently", frequentApp)
            functionsClass.saveFile(frequentApp + "Frequently", frequentApp)
        }

        functionsClass.saveFileAppendLine(".categoryInfo", "Frequently")

        functionsClass.addAppShortcuts()

        val categoryIntent = Intent(applicationContext, FoldersConfigurations::class.java)
        startActivity(categoryIntent)
    } else {//Floating Shortcuts
        PublicVariable.frequentlyUsedApps = frequentAppsArray
        PublicVariable.freqLength = PublicVariable.frequentlyUsedApps.size

        Intent(applicationContext, ApplicationsView::class.java).apply {
            putExtra("frequentApps", frequentAppsArray)
            putExtra("frequentAppsNumbers", frequentAppsArray.size)
            startActivity(this)
        }

        functionsClass.addAppShortcuts()
    }
}