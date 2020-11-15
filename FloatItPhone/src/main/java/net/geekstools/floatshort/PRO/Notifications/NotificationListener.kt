/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/15/20 5:14 AM
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
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import net.geekstools.floatshort.PRO.Utils.Functions.*
import net.geekstools.floatshort.PRO.Utils.Functions.Debug.Companion.PrintDebug

class NotificationListener : NotificationListenerService() {

    private val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    private val fileIO: FileIO by lazy {
        FileIO(applicationContext)
    }

    private val bitmapExtractor: BitmapExtractor by lazy {
        BitmapExtractor(applicationContext)
    }

    lateinit var broadcastReceiver: BroadcastReceiver

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

                notificationPackage = statusBarNotification.packageName
                notificationId = statusBarNotification.key
                notificationTime = statusBarNotification.postTime.toString()

                if (PublicVariable.previousDuplicated == null) {

                    PublicVariable.previousDuplicated = statusBarNotification.packageName

                } else {

                    if (PublicVariable.previousDuplicated == notificationPackage) {

                        Handler(Looper.getMainLooper()).postDelayed({

                            PublicVariable.previousDuplicated = null

                        }, 500)

                        return
                    }
                }

                val extras: Bundle = statusBarNotification.notification.extras

                statusBarNotification.notification.extras.takeIf {
                    (statusBarNotification.notification.extras != null)
                }.let {

                    notificationTitle = try {
                        extras.getString(Notification.EXTRA_TITLE)!!
                    } catch (e: Exception) {
                        functionsClassLegacy.applicationName(notificationPackage)
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

                                functionsClassLegacy.applicationIcon(notificationPackage)

                            }
                        }

                    } else {

                        notificationIcon = try {

                            functionsClassLegacy.bitmapToDrawable(statusBarNotification.notification.largeIcon)

                        } catch (e: Exception) {

                            functionsClassLegacy.applicationIcon(notificationPackage)

                        }
                    }

                }

                Debug.PrintDebug("::: Package ::: $notificationPackage")
                Debug.PrintDebug("::: Key ::: $notificationId")
                Debug.PrintDebug("::: Title ::: $notificationTitle")
                Debug.PrintDebug("::: Text ::: $notificationText")
                Debug.PrintDebug("::: Time ::: $notificationTime")

                /*Save Temp Notification Files*/
                fileIO.saveFileAppendLine(notificationPackage + "_" + "Notification" + "Package", notificationTime)
                fileIO.saveFile(notificationTime + "_" + "Notification" + "Key", notificationId)
                fileIO.saveFile(notificationTime + "_" + "Notification" + "Title", notificationTitle)
                fileIO.saveFile(notificationTime + "_" + "Notification" + "Text", notificationText.toString())
                bitmapExtractor.saveBitmapIcon(notificationTime + "_" + "Notification" + "Icon", notificationIcon)
                PublicVariable.notificationIntent[notificationTime] = statusBarNotification.notification.contentIntent


                val intentFilter = IntentFilter()
                intentFilter.addAction("Remove_Notification_Key")
                broadcastReceiver = object : BroadcastReceiver() {

                    override fun onReceive(context: Context, intent: Intent?) {

                        intent?.let {

                            if (intent.action == "Remove_Notification_Key") {

                                this@NotificationListener
                                        .cancelNotification(intent.getStringExtra("notification_key"))
                            }
                        }
                    }
                }

                try {
                    registerReceiver(broadcastReceiver, intentFilter)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

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

        try {
            unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        statusBarNotification?.let {

            if (statusBarNotification.packageName != null
                    && statusBarNotification.isClearable) {

                val notificationPackage = statusBarNotification.packageName
                val notificationTime = statusBarNotification.postTime.toString()

                Debug.PrintDebug("::: Remove Package ::: $notificationPackage")
                Debug.PrintDebug("::: Remove Time ::: $notificationTime")

                deleteFile(notificationTime + "_" + "Notification" + "Key")
                deleteFile(notificationTime + "_" + "Notification" + "Title")
                deleteFile(notificationTime + "_" + "Notification" + "Text")
                deleteFile(notificationTime + "_" + "Notification" + "Icon")

                fileIO.removeLine(notificationPackage + "_" + "Notification" + "Package", notificationTime)

                if (fileIO.fileLinesCounter(notificationPackage + "_" + "Notification" + "Package") == 0) {

                    deleteFile(notificationPackage + "_" + "Notification" + "Package")

                    sendBroadcast(Intent("Notification_Dot_No").putExtra("NotificationPackage", notificationPackage))

                    PublicVariable.notificationIntent.clear()
                }

                PublicVariable.notificationIntent.remove(notificationTime)
            }

        }

    }
}