/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/28/20 7:22 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Notifications

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug.Companion.PrintDebug
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class NotificationListener : NotificationListenerService() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    lateinit var notificationTitle: String
    private var notificationText: String? = null

    lateinit var notificationPackage: String

    lateinit var notificationTime: String
    lateinit var notificationId: String

    lateinit var notificationIcon: Drawable

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onListenerConnected() {
        PrintDebug("Notification Listener Connected")
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        super.onNotificationPosted(statusBarNotification)

        statusBarNotification?.let {

            if (statusBarNotification.packageName != null
                    && !statusBarNotification.isClearable) {

                if (PublicVariable.previousDuplicated == null) {

                    PublicVariable.previousDuplicated = statusBarNotification.packageName

                } else {

                    if (PublicVariable.previousDuplicated == notificationPackage) {

                        Handler().postDelayed({

                            PublicVariable.previousDuplicated = null

                        }, 500)

                        return
                    }
                }

                notificationPackage = statusBarNotification.packageName
                notificationId = statusBarNotification.key
                notificationTime = statusBarNotification.postTime.toString()

                val extras: Bundle = statusBarNotification.notification.extras

                statusBarNotification.notification.extras.takeIf {
                    (statusBarNotification.notification.extras != null)
                }.let {

                    notificationTitle = try {
                        extras.getString(Notification.EXTRA_TITLE)!!
                    } catch (e: Exception) {
                        functionsClass.appName(notificationPackage)
                    }

                    /*LOAD TEXT CONTENT*/
                    notificationText = try {

                        extras.getString(Notification.EXTRA_TEXT)

                    } catch (e: Exception) {

                        extras.getString(Notification.EXTRA_BIG_TEXT)

                    }

                    /*LOAD ICON*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        notificationIcon = try {

                            statusBarNotification.notification.getLargeIcon().loadDrawable(applicationContext)

                        } catch (e: Exception) {

                            try {

                                statusBarNotification.notification.smallIcon.loadDrawable(applicationContext)

                            } catch (e: Exception) {

                                functionsClass.appIcon(notificationPackage)

                            }
                        }

                    } else {

                        notificationIcon = try {

                            functionsClass.bitmapToDrawable(statusBarNotification.notification.largeIcon)

                        } catch (e: java.lang.Exception) {
                            functionsClass.appIcon(notificationPackage)
                        }
                    }

                }

                FunctionsClassDebug.PrintDebug("::: Package ::: $notificationPackage")
                FunctionsClassDebug.PrintDebug("::: Key ::: $notificationId")
                FunctionsClassDebug.PrintDebug("::: Title ::: $notificationTitle")
                FunctionsClassDebug.PrintDebug("::: Text ::: $notificationText")
                FunctionsClassDebug.PrintDebug("::: Time ::: $notificationTime")

                /*Save Temp Notification Files*/
                functionsClass.saveFileAppendLine(notificationPackage + "_" + "Notification" + "Package", notificationTime)
                functionsClass.saveFile(notificationTime + "_" + "Notification" + "Key", notificationId)
                functionsClass.saveFile(notificationTime + "_" + "Notification" + "Title", notificationTitle)
                functionsClass.saveFile(notificationTime + "_" + "Notification" + "Text", notificationText)
                functionsClass.saveBitmapIcon(notificationTime + "_" + "Notification" + "Icon", notificationIcon)
                PublicVariable.notificationIntent[notificationTime] = statusBarNotification.notification.contentIntent


                val intentFilter = IntentFilter()
                intentFilter.addAction("Remove_Notification_Key")
                val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

                    override fun onReceive(context: Context, intent: Intent?) {

                        intent?.let {

                            if (intent.action == "Remove_Notification_Key") {

                                this@NotificationListener
                                        .cancelNotification(intent.getStringExtra("notification_key"))
                            }
                        }
                    }
                }

                registerReceiver(broadcastReceiver, intentFilter)

                sendBroadcast(Intent("Notification_Dot")
                        .putExtra("NotificationPackage", notificationPackage))

            }

        }


    }

    override fun getActiveNotifications(): Array<StatusBarNotification> {

        return super.getActiveNotifications()
    }

    override fun onNotificationRemoved(statusBarNotification: StatusBarNotification?) {
        super.onNotificationRemoved(statusBarNotification)

        statusBarNotification?.let {

            if (statusBarNotification.packageName != null
                    && statusBarNotification.isClearable) {

                val notificationPackage = statusBarNotification.packageName
                val notificationTime = statusBarNotification.postTime.toString()

                FunctionsClassDebug.PrintDebug("::: Remove Package ::: $notificationPackage")
                FunctionsClassDebug.PrintDebug("::: Remove Time ::: $notificationTime")

                deleteFile(notificationTime + "_" + "Notification" + "Key")
                deleteFile(notificationTime + "_" + "Notification" + "Title")
                deleteFile(notificationTime + "_" + "Notification" + "Text")
                deleteFile(notificationTime + "_" + "Notification" + "Icon")

                functionsClass.removeLine(notificationPackage + "_" + "Notification" + "Package", notificationTime)

                if (functionsClass.countLineInnerFile(notificationPackage + "_" + "Notification" + "Package") == 0) {

                    deleteFile(notificationPackage + "_" + "Notification" + "Package")

                    sendBroadcast(Intent("Notification_Dot_No").putExtra("NotificationPackage", notificationPackage))

                    PublicVariable.notificationIntent.clear()
                }

                PublicVariable.notificationIntent.remove(notificationTime)
            }

        }

    }
}