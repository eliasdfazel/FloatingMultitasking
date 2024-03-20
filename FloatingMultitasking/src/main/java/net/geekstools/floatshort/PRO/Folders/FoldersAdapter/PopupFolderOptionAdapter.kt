/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Folders.FoldersAdapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import net.geekstools.floatshort.PRO.Checkpoint
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PreferencesIO
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Utils.UI.Splash.FloatingSplash
import net.geekstools.imageview.customshapes.ShapesImage

class PopupFolderOptionAdapter : BaseAdapter {

    private var context: Context

    private var functionsClassLegacy: FunctionsClassLegacy
    private var fileIO: FileIO
    private var preferencesIO: PreferencesIO

    private var securityFunctions: SecurityFunctions

    private var splitOne: Drawable? = null
    private var splitTwo: Drawable? = null

    private var loadCustomIcons: LoadCustomIcons? = null

    private var adapterItems: ArrayList<AdapterItems>

    private var folderName: String

    private var classNameCommand: String
    private var startId: Int

    private var layoutInflater = 0

    private var xPosition = 0
    private var yPosition = 0

    private var HW = 0

    constructor(context: Context, adapterItems: ArrayList<AdapterItems>, folderName: String, classNameCommand: String, startId: Int) {
        this.context = context

        this.adapterItems = adapterItems

        this.folderName = folderName

        this.classNameCommand = classNameCommand
        this.startId = startId

        functionsClassLegacy = FunctionsClassLegacy(context)
        fileIO = FileIO(context)
        preferencesIO = PreferencesIO(context)

        securityFunctions = SecurityFunctions(context)

        PublicVariable.floatingSizeNumber = preferencesIO.readDefaultPreference("floatingSize", 39)
        when (functionsClassLegacy.shapesImageId()) {
            1 -> layoutInflater = R.layout.item_popup_category_droplet
            2 -> layoutInflater = R.layout.item_popup_category_circle
            3 -> layoutInflater = R.layout.item_popup_category_square
            4 -> layoutInflater = R.layout.item_popup_category_squircle
            0 -> layoutInflater = R.layout.item_popup_category_noshape
        }
        if (functionsClassLegacy.customIconsEnable()) {
            loadCustomIcons = LoadCustomIcons(context, functionsClassLegacy.customIconPackageName())
        }
    }

    constructor(context: Context, adapterItems: ArrayList<AdapterItems>, folderName: String, classNameCommand: String, startId: Int,
                xPosition: Int, yPosition: Int, HW: Int) {
        this.context = context
        this.adapterItems = adapterItems
        this.folderName = folderName
        this.classNameCommand = classNameCommand
        this.startId = startId
        this.xPosition = xPosition
        this.yPosition = yPosition
        this.HW = HW

        functionsClassLegacy = FunctionsClassLegacy(context)
        fileIO = FileIO(context)
        preferencesIO = PreferencesIO(context)

        securityFunctions = SecurityFunctions(context)

        PublicVariable.floatingSizeNumber = preferencesIO.readDefaultPreference("floatingSize", 39)

        when (functionsClassLegacy.shapesImageId()) {
            1 -> layoutInflater = R.layout.item_popup_category_droplet
            2 -> layoutInflater = R.layout.item_popup_category_circle
            3 -> layoutInflater = R.layout.item_popup_category_square
            4 -> layoutInflater = R.layout.item_popup_category_squircle
            0 -> layoutInflater = R.layout.item_popup_category_noshape
        }

        if (functionsClassLegacy.customIconsEnable()) {
            loadCustomIcons = LoadCustomIcons(context, functionsClassLegacy.customIconPackageName())
        }
    }

    override fun getCount(): Int {
        return adapterItems.size
    }

    override fun getItem(position: Int): Any {

        return adapterItems[position]
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getView(position: Int, initialConvertView: View?, parent: ViewGroup?): View? {
        var convertView = initialConvertView

        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(layoutInflater, null)

            viewHolder = ViewHolder()
            viewHolder.items = convertView.findViewById<View>(R.id.items) as RelativeLayout
            viewHolder.imgIcon = convertView.findViewById<View>(R.id.iconViewItem) as ShapesImage
            viewHolder.split_one = convertView.findViewById<View>(R.id.split_one) as ShapesImage
            viewHolder.split_two = convertView.findViewById<View>(R.id.split_two) as ShapesImage
            viewHolder.textAppName = convertView.findViewById<View>(R.id.titleViewItem) as TextView

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        try {
            if (context.getFileStreamPath(adapterItems[position].packageName + ".SplitOne").exists()
                    && context.getFileStreamPath(adapterItems[position].packageName + ".SplitTwo").exists()
                    && adapterItems[position].appName == context.getString(R.string.splitIt)) {

                splitOne = if (functionsClassLegacy.customIconsEnable()) {
                    loadCustomIcons!!.getDrawableIconForPackage(fileIO.readFile(adapterItems[position].packageName + ".SplitOne"), functionsClassLegacy.shapedAppIcon(fileIO.readFile(adapterItems[position].packageName + ".SplitOne")))
                } else {
                    functionsClassLegacy.shapedAppIcon(fileIO.readFile(adapterItems[position].packageName + ".SplitOne"))
                }
                splitTwo = if (functionsClassLegacy.customIconsEnable()) {
                    loadCustomIcons!!.getDrawableIconForPackage(fileIO.readFile(adapterItems[position].packageName + ".SplitTwo"), functionsClassLegacy.shapedAppIcon(fileIO.readFile(adapterItems[position].packageName + ".SplitTwo")))
                } else {
                    functionsClassLegacy.shapedAppIcon(fileIO.readFile(adapterItems[position].packageName + ".SplitTwo"))
                }
                viewHolder.split_one!!.setImageDrawable(splitOne)
                viewHolder.split_two!!.setImageDrawable(splitTwo)

                viewHolder.split_one!!.imageAlpha = preferencesIO.readDefaultPreference("autoTrans", 255)
                viewHolder.split_two!!.imageAlpha = preferencesIO.readDefaultPreference("autoTrans", 255)

            } else if (adapterItems[position].appName == context.getString(R.string.splitIt)) {

                splitOne = if (functionsClassLegacy.customIconsEnable()) {
                    loadCustomIcons!!.getDrawableIconForPackage(fileIO.readFileLinesAsArray(adapterItems[position].packageName)?.get(0)
                            ?:"null", functionsClassLegacy.shapedAppIcon(fileIO.readFileLinesAsArray(adapterItems[position].packageName)?.get(0)
                            ?:"null"))
                } else {
                    functionsClassLegacy.shapedAppIcon(fileIO.readFileLinesAsArray(adapterItems[position].packageName)?.get(0)
                            ?:"null")
                }

                splitTwo = if (functionsClassLegacy.customIconsEnable()) {
                    loadCustomIcons!!.getDrawableIconForPackage(fileIO.readFileLinesAsArray(adapterItems[position].packageName)?.get(1)
                            ?:"null", functionsClassLegacy.shapedAppIcon(fileIO.readFileLinesAsArray(adapterItems[position].packageName)?.get(1)
                            ?:"null"))
                } else {
                    functionsClassLegacy.shapedAppIcon(fileIO.readFileLinesAsArray(adapterItems[position].packageName)?.get(1)
                            ?:"null")
                }

                viewHolder.split_one!!.setImageDrawable(splitOne)
                viewHolder.split_two!!.setImageDrawable(splitTwo)
                viewHolder.split_one!!.imageAlpha = preferencesIO.readDefaultPreference("autoTrans", 255)
                viewHolder.split_two!!.imageAlpha = preferencesIO.readDefaultPreference("autoTrans", 255)
            } else {
                viewHolder!!.split_one!!.setImageDrawable(null)
                viewHolder.split_two!!.setImageDrawable(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        viewHolder!!.imgIcon!!.setImageDrawable(adapterItems[position].appIcon)
        viewHolder.textAppName!!.text = adapterItems[position].appName
        viewHolder.imgIcon!!.imageAlpha = preferencesIO.readDefaultPreference("autoTrans", 255)
        viewHolder.textAppName!!.alpha = if (preferencesIO.readDefaultPreference("autoTrans", 255) < 130) 0.70f else 1.0f

        val itemsListColor: Int = PublicVariable.colorLightDark

        val drawPopupShortcut = context.getDrawable(R.drawable.popup_shortcut_whole) as LayerDrawable?
        val backPopupShortcut = drawPopupShortcut!!.findDrawableByLayerId(R.id.backgroundTemporary)
        backPopupShortcut.setTint(itemsListColor)
        viewHolder.items!!.background = drawPopupShortcut
        viewHolder.textAppName!!.setTextColor(PublicVariable.colorLightDarkOpposite)
        convertView?.setOnClickListener { view ->
            try {
                if (adapterItems[position].appName.contains(context.getString(R.string.edit_folder))) {
                    context.startActivity(Intent(context, FoldersConfigurations::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } else if (adapterItems[position].appName.contains(context.getString(R.string.remove_folder))) {
                    context.sendBroadcast(Intent("Remove_Category_$classNameCommand").putExtra("startId", startId))
                } else if (adapterItems[position].appName.contains(context.getString(R.string.unpin_folder))) {
                    context.sendBroadcast(Intent("Unpin_App_$classNameCommand").putExtra("startId", startId))
                } else if (adapterItems[position].appName.contains(context.getString(R.string.pin_folder))) {
                    context.sendBroadcast(Intent("Pin_App_$classNameCommand").putExtra("startId", startId))
                } else if (adapterItems[position].appName.contains(context.getString(R.string.splitIt))) {
                    if (functionsClassLegacy.securityServicesSubscribed()) {

                        SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                            override fun authenticatedFloatIt(extraInformation: Bundle?) {
                                super.authenticatedFloatIt(extraInformation)
                                Log.d(this@PopupFolderOptionAdapter.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                                if (!functionsClassLegacy.AccessibilityServiceEnabled() && !functionsClassLegacy.SettingServiceRunning(InteractionObserver::class.java)) {
                                    context.startActivity(Intent(context, Checkpoint::class.java)
                                            .putExtra(context.getString(R.string.splitIt), context.packageName)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                } else {
                                    val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

                                    val accessibilityEvent = AccessibilityEvent.obtain()
                                    accessibilityEvent.setSource(view)
                                    accessibilityEvent.eventType = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                                    accessibilityEvent.action = 10296
                                    accessibilityEvent.className = classNameCommand
                                    accessibilityEvent.text.add(context.packageName)

                                    accessibilityManager.sendAccessibilityEvent(accessibilityEvent)
                                }
                            }

                            override fun failedAuthenticated() {
                                super.failedAuthenticated()
                                Log.d(this@PopupFolderOptionAdapter.javaClass.simpleName, "FailedAuthenticated")


                            }

                            override fun invokedPinPassword() {
                                super.invokedPinPassword()
                                Log.d(this@PopupFolderOptionAdapter.javaClass.simpleName, "InvokedPinPassword")

                            }
                        }

                        context.startActivity(Intent(context, AuthenticationFingerprint::class.java).apply {
                            putExtra("OtherTitle", context.getString(R.string.securityServices))
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }, ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, 0).toBundle())

                    } else {
                        if (!functionsClassLegacy.AccessibilityServiceEnabled() && !functionsClassLegacy.SettingServiceRunning(InteractionObserver::class.java)) {
                            context.startActivity(Intent(context, Checkpoint::class.java)
                                    .putExtra(context.getString(R.string.splitIt), context.packageName)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        } else {
                            val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

                            val accessibilityEvent = AccessibilityEvent.obtain()
                            accessibilityEvent.setSource(view)
                            accessibilityEvent.eventType = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                            accessibilityEvent.action = 10296
                            accessibilityEvent.className = classNameCommand
                            accessibilityEvent.text.add(context.packageName)

                            accessibilityManager.sendAccessibilityEvent(accessibilityEvent)
                        }
                    }
                } else { //Open Applications
                    if (securityFunctions.isAppLocked(adapterItems[position].packageName) || securityFunctions.isAppLocked(folderName)) {

                        SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                            override fun authenticatedFloatIt(extraInformation: Bundle?) {
                                super.authenticatedFloatIt(extraInformation)
                                Log.d(this@PopupFolderOptionAdapter.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                                if (functionsClassLegacy.splashReveal()) {
                                    val splashReveal = Intent(context, FloatingSplash::class.java)
                                    splashReveal.putExtra("PackageName", adapterItems[position].packageName)
                                    splashReveal.putExtra("X", xPosition)
                                    splashReveal.putExtra("Y", yPosition)
                                    splashReveal.putExtra("HW", HW)
                                    context.startService(splashReveal)
                                } else {
                                    if (functionsClassLegacy.FreeForm()) {
                                        functionsClassLegacy.openApplicationFreeForm(adapterItems[position].packageName,
                                                xPosition,
                                                functionsClassLegacy.displayX() / 2,
                                                yPosition,
                                                functionsClassLegacy.displayY() / 2
                                        )
                                    } else {
                                        functionsClassLegacy.appsLaunchPad(adapterItems[position].packageName)
                                    }
                                }
                            }

                            override fun failedAuthenticated() {
                                super.failedAuthenticated()
                                Log.d(this@PopupFolderOptionAdapter.javaClass.simpleName, "FailedAuthenticated")


                            }

                            override fun invokedPinPassword() {
                                super.invokedPinPassword()
                                Log.d(this@PopupFolderOptionAdapter.javaClass.simpleName, "InvokedPinPassword")

                            }
                        }

                        context.startActivity(Intent(context, AuthenticationFingerprint::class.java).apply {
                            putExtra("OtherTitle", context.getString(R.string.securityServices))
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }, ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, 0).toBundle())

                    } else {
                        if (functionsClassLegacy.splashReveal()) {
                            val splashReveal = Intent(context, FloatingSplash::class.java)
                            splashReveal.putExtra("PackageName", adapterItems[position].packageName)
                            splashReveal.putExtra("X", xPosition)
                            splashReveal.putExtra("Y", yPosition)
                            splashReveal.putExtra("HW", HW)
                            context.startService(splashReveal)
                        } else {
                            if (functionsClassLegacy.FreeForm()) {
                                functionsClassLegacy.openApplicationFreeForm(adapterItems[position].packageName,
                                        xPosition,
                                        functionsClassLegacy.displayX() / 2,
                                        yPosition,
                                        functionsClassLegacy.displayY() / 2
                                )
                            } else {
                                functionsClassLegacy.appsLaunchPad(adapterItems[position].packageName)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            context.sendBroadcast(Intent("Hide_PopupListView_Category" + context.getPackageName()))
        }
        convertView?.setOnLongClickListener { view ->
            if (functionsClassLegacy.returnAPI() >= 24) {
                if (!functionsClassLegacy.AccessibilityServiceEnabled() && !functionsClassLegacy.SettingServiceRunning(InteractionObserver::class.java)) {
                    context.startActivity(Intent(context, Checkpoint::class.java)
                            .putExtra(context.getString(R.string.splitIt), context.packageName)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } else {
                    PublicVariable.splitSinglePackage = adapterItems[position].packageName
                    if (functionsClassLegacy.appIsInstalled(PublicVariable.splitSinglePackage)) {
                        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
                        val accessibilityEvent = AccessibilityEvent.obtain()
                        accessibilityEvent.setSource(view)
                        accessibilityEvent.eventType = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                        accessibilityEvent.action = 69201
                        accessibilityEvent.className = classNameCommand
                        accessibilityEvent.text.add(context.packageName)
                        accessibilityManager.sendAccessibilityEvent(accessibilityEvent)
                    }
                }
            }
            context.sendBroadcast(Intent("Hide_PopupListView_Category" + context.getPackageName()))
            true
        }
        return convertView
    }

    internal class ViewHolder {
        var items: RelativeLayout? = null
        var imgIcon: ShapesImage? = null
        var split_one: ShapesImage? = null
        var split_two: ShapesImage? = null
        var textAppName: TextView? = null
    }
}