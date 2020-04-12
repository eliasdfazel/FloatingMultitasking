/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 12:47 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Notifications.PopupAdapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.UserInterfaceExtraData
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug.Companion.PrintDebug
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.Splash.FloatingSplash
import net.geekstools.imageview.customshapes.ShapesImage
import java.util.*
import kotlin.math.abs

class PopupShortcutsNotification(private val context: Context,
                                 private val adapterItems: ArrayList<AdapterItems>,
                                 private val className: String, private val packageName: String,
                                 private val iconColor: Int,
                                 private val startId: Int,
                                 private val xPosition: Int, private val yPosition: Int, private val HW: Int) : BaseAdapter() {

    var functionsClass: FunctionsClass = FunctionsClass(context)
    var securityFunctions: SecurityFunctions = SecurityFunctions(context)

    private var layoutInflator = 0

    init {
        layoutInflator = when (functionsClass.shapesImageId()) {
            1 -> R.layout.item_popup_notification_droplet
            2 -> R.layout.item_popup_notification_circle
            3 -> R.layout.item_popup_notification_square
            4 -> R.layout.item_popup_notification_squircle
            0 -> R.layout.item_popup_notification_noshape
            else -> R.layout.item_popup_notification_noshape
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

            val layoutInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(layoutInflator, null)

            viewHolder = ViewHolder()
            viewHolder.notificationItem = convertView.findViewById<View>(R.id.notificationItem) as RelativeLayout
            viewHolder.notificationBanner = convertView.findViewById<View>(R.id.notificationBanner) as RelativeLayout
            viewHolder.notificationContent = convertView.findViewById<View>(R.id.notificationContent) as RelativeLayout
            viewHolder.notificationAppIcon = convertView.findViewById<View>(R.id.notificationAppIcon) as ShapesImage
            viewHolder.notificationLargeIcon = convertView.findViewById<View>(R.id.notificationLargeIcon) as ShapesImage
            viewHolder.notificationAppName = convertView.findViewById<View>(R.id.notificationAppName) as TextView
            viewHolder.notificationTitle = convertView.findViewById<View>(R.id.notificationTitle) as TextView
            viewHolder.notificationText = convertView.findViewById<View>(R.id.notificationText) as TextView

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val itemBackground: Int = if (functionsClass.appThemeTransparent() == true) {
            functionsClass.setColorAlpha(PublicVariable.colorLightDark, 50f)
        } else {
            PublicVariable.colorLightDark
        }

        val drawPopupShortcut = context.getDrawable(R.drawable.popup_shortcut_whole) as LayerDrawable?
        val backgroundTemporary = drawPopupShortcut!!.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary.setTint(itemBackground)

        viewHolder.notificationItem!!.background = drawPopupShortcut
        viewHolder.notificationBanner!!.background = drawPopupShortcut
        viewHolder.notificationAppIcon!!.setImageDrawable(adapterItems[position].notificationAppIcon)
        viewHolder.notificationAppName!!.text = adapterItems[position].notificationAppName
        viewHolder.notificationAppName!!.append("   " + context.getString(R.string.notificationEmoji))
        viewHolder.notificationLargeIcon!!.setImageDrawable(adapterItems[position].notificationLargeIcon)
        viewHolder.notificationTitle!!.text = adapterItems[position].notificationTitle
        viewHolder.notificationText!!.text = adapterItems[position].notificationText

        if (PublicVariable.themeLightDark) {
            viewHolder.notificationAppName!!.setTextColor(functionsClass.manipulateColor(functionsClass.extractVibrantColor(adapterItems[position].notificationAppIcon), 0.50f))
        } else {
            viewHolder.notificationAppName!!.setTextColor(functionsClass.manipulateColor(functionsClass.extractVibrantColor(adapterItems[position].notificationAppIcon), 1.30f))
        }

        viewHolder.notificationTitle!!.setTextColor(PublicVariable.colorLightDarkOpposite)
        viewHolder.notificationText!!.setTextColor(PublicVariable.colorLightDarkOpposite)
        convertView?.setOnClickListener {
            if (securityFunctions.isAppLocked(packageName)) {

                SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                    override fun authenticatedFloatIt(extraInformation: Bundle?) {
                        super.authenticatedFloatIt(extraInformation)
                        Log.d(this@PopupShortcutsNotification.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                        if (functionsClass.splashReveal()) {
                            val splashReveal = Intent(context, FloatingSplash::class.java)
                            splashReveal.putExtra("packageName", packageName)
                            splashReveal.putExtra("X", xPosition)
                            splashReveal.putExtra("Y", yPosition)
                            splashReveal.putExtra("HW", HW)
                            splashReveal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startService(splashReveal)
                        } else {

                            if (functionsClass.FreeForm()) {
                                functionsClass.openApplicationFreeForm(packageName,
                                        xPosition,
                                        functionsClass.displayX() / 2,
                                        yPosition,
                                        functionsClass.displayY() / 2
                                )
                            } else {
                                functionsClass.appsLaunchPad(packageName)
                            }
                        }
                    }

                    override fun failedAuthenticated() {
                        super.failedAuthenticated()
                        Log.d(this@PopupShortcutsNotification.javaClass.simpleName, "FailedAuthenticated")

                    }

                    override fun invokedPinPassword() {
                        super.invokedPinPassword()
                        Log.d(this@PopupShortcutsNotification.javaClass.simpleName, "InvokedPinPassword")

                    }
                }

                context.startActivity(Intent(context, AuthenticationFingerprint::class.java).apply {
                    putExtra(UserInterfaceExtraData.OtherTitle, functionsClass.appName(packageName))
                    putExtra(UserInterfaceExtraData.PrimaryColor, (iconColor))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }, ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, 0).toBundle())

            } else {

                if (functionsClass.splashReveal()) {
                    val splashReveal = Intent(context, FloatingSplash::class.java)
                    splashReveal.putExtra("packageName", packageName)
                    splashReveal.putExtra("X", xPosition)
                    splashReveal.putExtra("Y", yPosition)
                    splashReveal.putExtra("HW", HW)
                    splashReveal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startService(splashReveal)
                } else {

                    if (functionsClass.FreeForm()) {
                        functionsClass.openApplicationFreeForm(packageName,
                                xPosition,
                                functionsClass.displayX() / 2,
                                yPosition,
                                functionsClass.displayY() / 2
                        )
                    } else {
                        functionsClass.appsLaunchPad(packageName)
                    }
                }
            }
            context.sendBroadcast(Intent("Hide_PopupListView_Shortcuts_Notification"))
        }

        convertView?.setOnTouchListener(object : OnTouchListener {

            var xPosition = 0f
            var xMovePosition = 0f

            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

                val X = functionsClass.DpToInteger(37)

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> xPosition = motionEvent.x
                    MotionEvent.ACTION_MOVE -> {
                        xMovePosition = motionEvent.x
                        val minus = (xMovePosition - xPosition).toInt()
                        if (minus < 0) {
                            if (abs(minus) > X) {
                                viewHolder.notificationContent!!.animate().translationX(-xPosition)

                                try {
                                    val notificationPackage: String = adapterItems[position].notificationPackage
                                    val notificationTime: String = adapterItems[position].notificationTime

                                    PrintDebug("::: Remove Package ::: $notificationPackage")
                                    PrintDebug("::: Remove Time ::: $notificationTime")
                                    PrintDebug("::: Broadcast Remove Key ::: " + adapterItems[position].notificationId)

                                    context.deleteFile(notificationTime + "_" + "Notification" + "Key")
                                    context.deleteFile(notificationTime + "_" + "Notification" + "Title")
                                    context.deleteFile(notificationTime + "_" + "Notification" + "Text")
                                    context.deleteFile(notificationTime + "_" + "Notification" + "Icon")

                                    functionsClass.removeLine(notificationPackage + "_" + "Notification" + "Package", notificationTime)
                                    if (functionsClass.countLineInnerFile(notificationPackage + "_" + "Notification" + "Package") == 0) {
                                        context.deleteFile(notificationPackage + "_" + "Notification" + "Package")

                                        context.sendBroadcast(Intent("Notification_Dot_No").putExtra("NotificationPackage", notificationPackage))

                                        PublicVariable.notificationIntent.clear()
                                    }

                                    PublicVariable.notificationIntent.remove(notificationTime)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                context.sendBroadcast(Intent("Remove_Notification_Key")
                                        .putExtra("notification_key", adapterItems[position].notificationId)
                                )
                                context.sendBroadcast(Intent("Hide_PopupListView_Shortcuts_Notification"))
                            }
                        } else {
                            if (abs(minus) > X) {
                                viewHolder.notificationContent!!.animate().translationX(xPosition)

                                try {
                                    val notificationPackage: String = adapterItems[position].notificationPackage
                                    val notificationTime: String = adapterItems[position].notificationTime

                                    PrintDebug("::: Remove Package ::: $notificationPackage")
                                    PrintDebug("::: Remove Time ::: $notificationTime")
                                    PrintDebug("::: Broadcast Remove Key ::: " + adapterItems[position].notificationId)

                                    context.deleteFile(notificationTime + "_" + "Notification" + "Key")
                                    context.deleteFile(notificationTime + "_" + "Notification" + "Title")
                                    context.deleteFile(notificationTime + "_" + "Notification" + "Text")
                                    context.deleteFile(notificationTime + "_" + "Notification" + "Icon")

                                    functionsClass.removeLine(notificationPackage + "_" + "Notification" + "Package", notificationTime)
                                    if (functionsClass.countLineInnerFile(notificationPackage + "_" + "Notification" + "Package") == 0) {
                                        context.deleteFile(notificationPackage + "_" + "Notification" + "Package")

                                        context.sendBroadcast(Intent("Notification_Dot_No").putExtra("NotificationPackage", notificationPackage))

                                        PublicVariable.notificationIntent.clear()
                                    }

                                    PublicVariable.notificationIntent.remove(notificationTime)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                context.sendBroadcast(Intent("Remove_Notification_Key")
                                        .putExtra("notification_key", adapterItems[position].notificationId)
                                )
                                context.sendBroadcast(Intent("Hide_PopupListView_Shortcuts_Notification"))
                            }
                        }
                    }
                }
                return false
            }
        })
        return convertView
    }

    internal class ViewHolder {
        var notificationItem: RelativeLayout? = null
        var notificationBanner: RelativeLayout? = null
        var notificationContent: RelativeLayout? = null
        var notificationAppIcon: ShapesImage? = null
        var notificationLargeIcon: ShapesImage? = null
        var notificationAppName: TextView? = null
        var notificationTitle: TextView? = null
        var notificationText: TextView? = null
    }
}