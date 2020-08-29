/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widgets.Utils

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.UserInterfaceExtraData
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.FloatingWidgetHomeScreenShortcuts
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widgets.WidgetConfigurations
import java.util.*

class FunctionsClassWidgets (private val context: Context) {

    fun handleOrientationWidgetLandscape(widgetIdentifier: String): WindowManager.LayoutParams {
        val layoutParams: WindowManager.LayoutParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT)
        } else {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT)
        }

        val initWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 213f, context.resources.displayMetrics).toInt()
        val initHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 133f, context.resources.displayMetrics).toInt()

        val sharedPrefPosition: SharedPreferences = context.getSharedPreferences(widgetIdentifier, Context.MODE_PRIVATE)

        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.width = sharedPrefPosition.getInt("WidgetWidth", initWidth)
        layoutParams.height = sharedPrefPosition.getInt("WidgetHeight", initHeight)
        layoutParams.x = sharedPrefPosition.getInt("Y", 0)
        layoutParams.y = sharedPrefPosition.getInt("X", 0)
        layoutParams.windowAnimations = android.R.style.Animation_Dialog

        val editor = sharedPrefPosition.edit()
        editor.putInt("X", layoutParams.x)
        editor.putInt("Y", layoutParams.y)
        editor.apply()

        return layoutParams
    }

    fun handleOrientationWidgetPortrait(widgetIdentifier: String): WindowManager.LayoutParams {
        val layoutParams: WindowManager.LayoutParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT)
        } else {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT)
        }

        val initWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 213f, context.resources.displayMetrics).toInt()
        val initHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 133f, context.resources.displayMetrics).toInt()

        val sharedPrefPosition: SharedPreferences = context.getSharedPreferences(widgetIdentifier, Context.MODE_PRIVATE)

        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.width = sharedPrefPosition.getInt("WidgetWidth", initWidth)
        layoutParams.height = sharedPrefPosition.getInt("WidgetHeight", initHeight)
        layoutParams.x = sharedPrefPosition.getInt("Y", 0)
        layoutParams.y = sharedPrefPosition.getInt("X", 0)
        layoutParams.windowAnimations = android.R.style.Animation_Dialog

        val editor = sharedPrefPosition.edit()
        editor.putInt("X", layoutParams.x)
        editor.putInt("Y", layoutParams.y)
        editor.apply()

        return layoutParams
    }

    private object PopupWidgetMenuItem {
        const val DELETE_ITEM: Int = 0
        const val EDIT_RECOVERY: Int = 1
        const val CREATE_HOME_SCREEN_SHORTCUT: Int = 2
        const val SECURITY_OPTION: Int = 3
    }

    fun popupOptionWidget(widgetConfigurations: WidgetConfigurations, context: Context, anchorView: View,
                          packageName: String, providerClassName: String, widgetId: Int,
                          widgetLabel: String, widgetPreview: Drawable,
                          addedWidgetRecovery: Boolean,
                          functionsClassLegacy: FunctionsClassLegacy) {

        val popupMenu = if (PublicVariable.themeLightDark) {
            PopupMenu(context, anchorView, Gravity.CENTER, 0, R.style.GeeksEmpire_Dialogue_Category_Light)
        } else {
            PopupMenu(context, anchorView, Gravity.CENTER, 0, R.style.GeeksEmpire_Dialogue_Category_Dark)
        }

        val menuItems: Array<String> = if (addedWidgetRecovery) {
            context.resources.getStringArray(R.array.ContextMenuWidgetRemove)
        } else {
            context.resources.getStringArray(R.array.ContextMenuWidget)
        }

        val popupItemIcon = (if (functionsClassLegacy.returnAPI() >= 28) {
            functionsClassLegacy.resizeDrawable(context.getDrawable(R.drawable.w_pref_popup), 100, 100)
        } else {
            context.getDrawable(R.drawable.w_pref_popup)
        })
        popupItemIcon?.setTint(functionsClassLegacy.extractVibrantColor(functionsClassLegacy.applicationIcon(packageName)))

        for (itemId in menuItems.indices) {

            popupMenu.menu
                    .add(Menu.NONE, itemId, itemId, Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + menuItems[itemId] + "</font>"))
                    .icon = popupItemIcon

        }

        val securityFunctions = SecurityFunctions(context)

        if (securityFunctions.isAppLocked(packageName + providerClassName)) {

            popupMenu.menu
                    .add(Menu.NONE, menuItems.size, menuItems.size, Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + context.getString(R.string.unLockIt) + "</font>"))
                    .icon = popupItemIcon

        } else {

            popupMenu.menu
                    .add(Menu.NONE, menuItems.size, menuItems.size, Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + context.getString(R.string.lockIt) + "</font>"))
                    .icon = popupItemIcon

        }

        try {
            val fields = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field[popupMenu]
                    val classPopupHelper = Class.forName(menuPopupHelper
                            .javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", Boolean::class.javaPrimitiveType)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                PopupWidgetMenuItem.DELETE_ITEM -> {

                    CoroutineScope(Dispatchers.Main).launch {

                        val widgetDataInterface = Room.databaseBuilder(context, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                .fallbackToDestructiveMigration()
                                .addCallback(object : RoomDatabase.Callback() {

                                    override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                        super.onOpen(supportSQLiteDatabase)

                                        widgetConfigurations.forceLoadConfiguredWidgets()

                                        removeWidgetToHomeScreen(FloatingWidgetHomeScreenShortcuts::class.java, packageName, widgetLabel, providerClassName)

                                    }
                                })
                                .build()

                        widgetDataInterface.initDataAccessObject().deleteByWidgetClassNameProviderWidgetSuspend(packageName, providerClassName)
                        widgetDataInterface.close()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            context.deleteSharedPreferences(providerClassName + packageName)
                            context.deleteSharedPreferences(widgetId.toString() + packageName)
                        }
                    }

                }
                PopupWidgetMenuItem.EDIT_RECOVERY -> {

                    CoroutineScope(Dispatchers.Main).launch {

                        val widgetDataInterface = Room.databaseBuilder(context, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                .fallbackToDestructiveMigration()
                                .build()

                        widgetDataInterface.initDataAccessObject().updateRecoveryByClassNameProviderWidgetSuspend(packageName, providerClassName, !addedWidgetRecovery)
                        widgetDataInterface.close()

                        widgetConfigurations.forceLoadConfiguredWidgets()

                    }

                }
                PopupWidgetMenuItem.CREATE_HOME_SCREEN_SHORTCUT -> {

                    widgetToHomeScreen(FloatingWidgetHomeScreenShortcuts::class.java,
                            packageName,
                            widgetLabel, widgetPreview,
                            providerClassName, functionsClassLegacy)

                }
                PopupWidgetMenuItem.SECURITY_OPTION -> {

                    if (securityFunctions.isAppLocked(packageName + providerClassName)) {

                        SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                            override fun invokedPinPassword() {

                            }

                            override fun failedAuthenticated() {

                            }

                            override fun authenticatedFloatIt(extraInformation: Bundle?) {

                                securityFunctions.doUnlockApps(packageName + providerClassName)
                            }
                        }

                        context.startActivity(Intent(context, AuthenticationFingerprint::class.java)
                                .putExtra(UserInterfaceExtraData.OtherTitle, widgetLabel)
                                .putExtra(UserInterfaceExtraData.DoLockUnlock, true)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, 0).toBundle())

                    } else {

                        if (functionsClassLegacy.securityServicesSubscribed()) {

                            securityFunctions.doLockApps(packageName + providerClassName)
                            securityFunctions.uploadLockedAppsData()

                        } else {

                            context.startActivity(Intent(context, InitializeInAppBilling::class.java)
                                    .putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.SubscriptionPurchase)
                                    .putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemSecurityServices)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle())
                        }

                    }
                }
            }

            true
        }

        popupMenu.show()
    }

    fun widgetToHomeScreen(className: Class<*>,
                           packageName: String, shortcutName: String?,
                           widgetPreviewDrawable: Drawable, providerClassName: String,
                           functionsClassLegacy: FunctionsClassLegacy) {

        val differentIntent = Intent(context, className)
        differentIntent.action = "CREATE_FLOATING_WIDGET_HOME_SCREEN_SHORTCUTS"
        differentIntent.addCategory(Intent.CATEGORY_DEFAULT)
        differentIntent.putExtra("PackageName", packageName)
        differentIntent.putExtra("ProviderClassName", providerClassName)
        differentIntent.putExtra("ShortcutLabel", shortcutName)

        val forNull = context.getDrawable(R.drawable.ic_launcher)
        forNull?.alpha = 0

        val widgetShortcutIcon = context.getDrawable(R.drawable.widget_home_screen_shortcuts) as LayerDrawable?
        try {
            widgetShortcutIcon?.setDrawableByLayerId(R.id.widgetPreviewHomeShortcut, widgetPreviewDrawable)
        } catch (e: Exception) {
            widgetShortcutIcon?.setDrawableByLayerId(R.id.widgetPreviewHomeShortcut, forNull)
        }

        try {
            if (widgetPreviewDrawable.intrinsicHeight < functionsClassLegacy.DpToInteger(77)) {

            } else {
                widgetShortcutIcon?.setDrawableByLayerId(R.id.appIconHomeShortcut, functionsClassLegacy.getAppIconDrawableCustomIcon(packageName))
            }
        } catch (e: Exception) {
            widgetShortcutIcon?.setDrawableByLayerId(R.id.appIconHomeShortcut, forNull)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val shortcutInfo = ShortcutInfo.Builder(context, packageName + providerClassName)
                    .setShortLabel(shortcutName!!)
                    .setLongLabel(shortcutName)
                    .setIcon(Icon.createWithBitmap(functionsClassLegacy.layerDrawableToBitmap(widgetShortcutIcon)))
                    .setIntent(differentIntent)
                    .build()

            context.getSystemService(ShortcutManager::class.java)?.let {

                it.requestPinShortcut(shortcutInfo, null)
            }

        } else {

            val addIntent = Intent()
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, differentIntent)
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName)
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, functionsClassLegacy.layerDrawableToBitmap(widgetShortcutIcon))
            addIntent.putExtra("duplicate", true)
            addIntent.action = "com.android.launcher.action.INSTALL_SHORTCUT"

            context.sendBroadcast(addIntent)
        }
    }

    fun removeWidgetToHomeScreen(className: Class<*>?, packageName: String, shortcutName: String?, providerClassName: String) {

        val differentIntent = Intent(context, className)
        differentIntent.action = Intent.ACTION_MAIN
        differentIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        differentIntent.putExtra("ShortcutsId", providerClassName)
        differentIntent.putExtra("ShortcutLabel", shortcutName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            context.getSystemService(ShortcutManager::class.java)?.let {
                val shortcutToDelete: MutableList<String> = ArrayList()
                shortcutToDelete.add(packageName + providerClassName)

                it.disableShortcuts(shortcutToDelete)
            }

        } else {

            val removeIntent = Intent()
            removeIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, differentIntent)
            removeIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName)
            removeIntent.putExtra("duplicate", true)
            removeIntent.action = "com.android.launcher.action.UNINSTALL_SHORTCUT"

            context.sendBroadcast(removeIntent)
        }
    }
}