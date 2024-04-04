/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:57 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter

import android.app.Activity
import android.app.ActivityOptions
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import net.geekstools.floatshort.PRO.Checkpoint
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsFloatingShortcutsPopuiOptions
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.SplitTransparentSingle

class FloatingShortcutsPopupOptionsAdapter : BaseAdapter {

    private val context: Context

    private val functionsClassLegacy: FunctionsClassLegacy
    private val securityFunctions: SecurityFunctions

    private val adapterItems: ArrayList<AdapterItemsFloatingShortcutsPopuiOptions>

    private val packageName: String
    private var className: String? = null

    private val classNameCommand: String
    private val startId: Int

    constructor(context: Context, adapterItems: ArrayList<AdapterItemsFloatingShortcutsPopuiOptions>,
                classNameCommand: String, packageName: String, startId: Int) {

        this.context = context

        this.adapterItems = adapterItems
        this.classNameCommand = classNameCommand

        this.packageName = packageName
        this.startId = startId

        functionsClassLegacy = FunctionsClassLegacy(context)
        securityFunctions = SecurityFunctions(context)
    }

    constructor(context: Context, adapterItems: ArrayList<AdapterItemsFloatingShortcutsPopuiOptions>,
                classNameCommand: String, packageName: String, className: String?, startId: Int) {

        this.context = context

        this.adapterItems = adapterItems

        this.classNameCommand = classNameCommand
        this.packageName = packageName

        this.className = className
        this.startId = startId

        functionsClassLegacy = FunctionsClassLegacy(context)
        securityFunctions = SecurityFunctions(context)
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
            val layoutInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            convertView = layoutInflater.inflate(R.layout.item_popup_app, null)

            viewHolder = ViewHolder()
            viewHolder.fullItem = convertView.findViewById<View>(R.id.items) as RelativeLayout
            viewHolder.iconViewItem = convertView.findViewById<View>(R.id.iconViewItem) as ImageView
            viewHolder.titleViewItem = convertView.findViewById<View>(R.id.titleViewItem) as TextView

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.iconViewItem!!.setImageDrawable(adapterItems[position].optionItemIcon)
        viewHolder.titleViewItem!!.text = adapterItems[position].optionItemTitle

        val itemsListColor = PublicVariable.colorLightDark

        val drawPopupShortcut = context.getDrawable(R.drawable.popup_shortcut_whole) as LayerDrawable?
        val backgroundTemporary = drawPopupShortcut!!.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary.setTint(itemsListColor)
        viewHolder.fullItem!!.background = drawPopupShortcut

        viewHolder.titleViewItem!!.setTextColor(PublicVariable.colorLightDarkOpposite)

        convertView?.setOnClickListener { view ->

            if (adapterItems[position].optionItemTitle == context.getString(R.string.splitIt)) {

                if (securityFunctions.isAppLocked(packageName)) {

                    SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                        override fun authenticatedFloatIt() {
                            super.authenticatedFloatIt()
                            Log.d(this@FloatingShortcutsPopupOptionsAdapter.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                            if (!functionsClassLegacy.AccessibilityServiceEnabled() && !functionsClassLegacy.SettingServiceRunning(InteractionObserver::class.java)) {
                                context.startActivity(Intent(context, Checkpoint::class.java)
                                        .putExtra(context.getString(R.string.splitIt), context.packageName)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            } else {
                                PublicVariable.splitSinglePackage = packageName
                                PublicVariable.splitSingleClassName = className

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

                        override fun failedAuthenticated() {
                            super.failedAuthenticated()
                            Log.d(this@FloatingShortcutsPopupOptionsAdapter.javaClass.simpleName, "FailedAuthenticated")


                        }

                        override fun invokedPinPassword() {
                            super.invokedPinPassword()
                            Log.d(this@FloatingShortcutsPopupOptionsAdapter.javaClass.simpleName, "InvokedPinPassword")

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
                        PublicVariable.splitSinglePackage = packageName
                        PublicVariable.splitSingleClassName = className

                        context.startActivity(Intent(context, SplitTransparentSingle::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

                    }
                }
            } else if (adapterItems[position].optionItemTitle == context.getString(R.string.pin)) {

                context.sendBroadcast(Intent("Pin_App_$classNameCommand").putExtra("startId", startId).setPackage(context.packageName))

            } else if (adapterItems[position].optionItemTitle == context.getString(R.string.unpin)) {

                context.sendBroadcast(Intent("Unpin_App_$classNameCommand").putExtra("startId", startId).setPackage(context.packageName))

            } else if (adapterItems[position].optionItemTitle == context.getString(R.string.floatIt)) {

                context.sendBroadcast(Intent("Float_It_$classNameCommand").putExtra("startId", startId).setPackage(context.packageName))

            } else if (adapterItems[position].optionItemTitle == context.getString(R.string.close)) {

                if (functionsClassLegacy.UsageStatsEnabled()) {
                    try {
                        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                        val queryUsageStats = usageStatsManager
                                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                                        System.currentTimeMillis() - 1000 * 60,  //begin
                                        System.currentTimeMillis()) //end
                        queryUsageStats.sortWith(Comparator { left, right ->

                            return@Comparator right.lastTimeUsed.compareTo(left.lastTimeUsed)
                        })
                        val inFrontPackageName = queryUsageStats[0].packageName
                        if (inFrontPackageName.contains(packageName)) {
                            val homeScreen = Intent(Intent.ACTION_MAIN)
                            homeScreen.addCategory(Intent.CATEGORY_HOME)
                            homeScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(homeScreen,
                                    ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (adapterItems[position].optionItemTitle == context.getString(R.string.remove)) {

                context.sendBroadcast(Intent("Remove_App_$classNameCommand").putExtra("startId", startId).setPackage(context.packageName))

            }

            context.sendBroadcast(Intent("Hide_PopupListView_Shortcuts" + context.getPackageName()).setPackage(context.packageName))
        }

        return convertView
    }

    internal class ViewHolder {
        var fullItem: RelativeLayout? = null
        var iconViewItem: ImageView? = null
        var titleViewItem: TextView? = null
    }
}