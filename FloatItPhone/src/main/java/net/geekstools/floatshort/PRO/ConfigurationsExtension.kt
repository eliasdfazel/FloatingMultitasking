package net.geekstools.floatshort.PRO

import android.R
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

fun ConfigurationsXYZ.checkUserInformation() {

    functionsClass.savePreference(".UserInformation", "isBetaTester", functionsClass.appVersionName(packageName).contains("[BETA]"))
    functionsClass.savePreference(".UserInformation", "installedVersionCode", functionsClass.appVersionCode(packageName))
    functionsClass.savePreference(".UserInformation", "installedVersionName", functionsClass.appVersionName(packageName))
    functionsClass.savePreference(".UserInformation", "deviceModel", functionsClass.deviceName)
    functionsClass.savePreference(".UserInformation", "userRegion", functionsClass.countryIso)

    if (functionsClass.appVersionName(packageName).contains("[BETA]")) {
        functionsClass.saveDefaultPreference("JoinedBetaProgrammer", true)
    }
}

fun ConfigurationsXYZ.initializeParameterUI() {

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