/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/14/20 4:37 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO

import android.content.Intent
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.Shortcuts.ApplicationsViewPhone
import net.geekstools.floatshort.PRO.Utils.Functions.IndexingProcess
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

fun Configurations.checkUserInformation() {

    preferencesIO.savePreference(".UserInformation", "isBetaTester", BuildConfig.VERSION_NAME.contains("[BETA]"))
    preferencesIO.savePreference(".UserInformation", "installedVersionCode", BuildConfig.VERSION_CODE)
    preferencesIO.savePreference(".UserInformation", "installedVersionName", BuildConfig.VERSION_NAME)
    preferencesIO.savePreference(".UserInformation", "deviceModel", functionsClassLegacy.deviceName)
    preferencesIO.savePreference(".UserInformation", "userRegion", functionsClassLegacy.countryIso)

    if (BuildConfig.VERSION_NAME.contains("[BETA]")) {
        preferencesIO.saveDefaultPreference("JoinedBetaProgrammer", true)
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

    functionsClassLegacy.extractWallpaperColor()
    functionsClassLegacy.loadSavedColor()
    functionsClassLegacy.checkLightDarkTheme()

    PublicVariable.floatingSizeNumber = preferencesIO.readDefaultPreference("floatingSize", 39)
    PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()
}

fun Configurations.triggerOpenProcess() {

    if (preferencesIO.readPreference("OpenMode", "openClassName", ApplicationsViewPhone::class.java.simpleName) == FoldersConfigurations::class.java.simpleName) {//Floating Folder

        Intent(applicationContext, FoldersConfigurations::class.java).apply {

            startActivity(this)
        }

    } else {//Floating Shortcuts

        Intent(applicationContext, ApplicationsViewPhone::class.java).apply {

            startActivity(this)
        }

        popupApplicationShortcuts.addPopupApplicationShortcuts()
    }
}

fun Configurations.triggerOpenProcessWithFrequentApps(frequentAppsArray: Array<String>) {

    if (frequentAppsArray.isNotEmpty()) {

        if (preferencesIO.readPreference("OpenMode", "openClassName", ApplicationsViewPhone::class.java.simpleName) == FoldersConfigurations::class.java.simpleName) {//Floating Folder

            if (getFileStreamPath("Frequently").exists()) {
                fileIO.removeLine(".categoryInfo", "Frequently")
                deleteFile("Frequently")
            }

            PublicVariable.frequentlyUsedApps = frequentAppsArray
            PublicVariable.freqLength = frequentAppsArray.size

            for (frequentApp in frequentAppsArray) {
                fileIO.saveFileAppendLine("Frequently", frequentApp)
                fileIO.saveFile(frequentApp + "Frequently", frequentApp)
            }

            fileIO.saveFileAppendLine(".categoryInfo", "Frequently")

            popupApplicationShortcuts.addPopupApplicationShortcuts()

            val categoryIntent = Intent(applicationContext, FoldersConfigurations::class.java)
            startActivity(categoryIntent)
        } else {//Floating Shortcuts
            PublicVariable.frequentlyUsedApps = frequentAppsArray
            PublicVariable.freqLength = PublicVariable.frequentlyUsedApps.size

            Intent(applicationContext, ApplicationsViewPhone::class.java).apply {
                putExtra("frequentApps", frequentAppsArray)
                putExtra("frequentAppsNumbers", frequentAppsArray.size)
                startActivity(this)
            }

            popupApplicationShortcuts.addPopupApplicationShortcuts()
        }

    }

}

fun Configurations.indexFloatingShortcuts() {

    if (getFileStreamPath(".uFile").exists()) {

        fileIO.readFileLinesAsArray(".uFile")?.let {

            val indexingProcess = IndexingProcess()

            it.forEach { lineContent ->

                indexingProcess.indexAppInfoShortcuts(
                        functionsClassLegacy.applicationName(lineContent) + " | " + lineContent
                )
            }
        }

        functionsClassLegacy.updateRecoverShortcuts()
    }
}