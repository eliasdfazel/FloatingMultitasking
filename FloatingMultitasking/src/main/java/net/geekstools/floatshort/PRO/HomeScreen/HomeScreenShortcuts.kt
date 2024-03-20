package net.geekstools.floatshort.PRO.HomeScreen

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.FloatIt

class HomeScreenShortcuts (private val context: Context) {

    fun create(packageName: String, className: String,
               functionsClassLegacy: FunctionsClassLegacy) {


        val differentIntent = Intent()
        differentIntent.setClass(context, FloatIt::class.java)
        differentIntent.action = "Float_It"
        differentIntent.addCategory(Intent.CATEGORY_DEFAULT)
        differentIntent.putExtra("PackageName", packageName)
        differentIntent.putExtra("ClassName", className)
        differentIntent.putExtra("ShortcutLabel", functionsClassLegacy.applicationName(packageName))

        val forNull = context.getDrawable(R.drawable.ic_launcher)
        forNull?.alpha = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val shortcutInfo = ShortcutInfo.Builder(context, packageName)
                .setShortLabel(functionsClassLegacy.applicationName(packageName)!!)
                .setLongLabel(functionsClassLegacy.applicationName(packageName))
                .setIcon(Icon.createWithBitmap(functionsClassLegacy.drawableToBitmap(functionsClassLegacy.getAppIconDrawableCustomIcon(packageName))))
                .setIntent(differentIntent)
                .build()

            context.getSystemService(ShortcutManager::class.java)
                .requestPinShortcut(shortcutInfo, null)

        } else {

            val addIntent = Intent().setPackage(context.packageName)
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, differentIntent)
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, functionsClassLegacy.applicationName(packageName))
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, functionsClassLegacy.drawableToBitmap(functionsClassLegacy.getAppIconDrawableCustomIcon(packageName)))
            addIntent.putExtra("duplicate", true)
            addIntent.action = "com.android.launcher.action.INSTALL_SHORTCUT"

            context.sendBroadcast(addIntent)
        }

    }

}