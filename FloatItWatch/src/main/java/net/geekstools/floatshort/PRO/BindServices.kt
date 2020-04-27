/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 10:24 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.text.Html
import androidx.annotation.RequiresApi

class BindServices : Service() {

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(333, bindServiceHigh())
        } else {
            startForeground(333, bindServiceLow())
        }

        return Service.START_STICKY
    }

    private fun bindServiceLow(): Notification {
        val notificationBuilder = Notification.Builder(this)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)

        notificationBuilder.setContentTitle(Html.fromHtml("<b><font color='" + getColor(R.color.light) + "'>" + resources.getString(R.string.bindTitle) + "</font></b>"))
        notificationBuilder.setContentText(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>" + resources.getString(R.string.bindDesc) + "</font>"))
        notificationBuilder.setTicker(resources.getString(R.string.app_name))
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        notificationBuilder.setLargeIcon(bitmap)
        notificationBuilder.setAutoCancel(false)
        notificationBuilder.setColor(getColor(R.color.default_color))
        notificationBuilder.setPriority(Notification.PRIORITY_DEFAULT)

        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0,
                Intent(this, Configurations::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT))

        return notificationBuilder.build()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun bindServiceHigh(): Notification {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        val notificationChannel = NotificationChannel(packageName, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)

        notificationManager
                ?.createNotificationChannel(notificationChannel)

        val notificationBuilder = Notification.Builder(this)
        notificationBuilder.setContentTitle(Html.fromHtml("<b><font color='" + getColor(R.color.light) + "'>" + resources.getString(R.string.bindTitle) + "</font></b>"))
        notificationBuilder.setContentText(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>" + resources.getString(R.string.bindDesc) + "</font>"))
        notificationBuilder.setTicker(resources.getString(R.string.app_name))
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher))
        notificationBuilder.setAutoCancel(false)
        notificationBuilder.setColor(getColor(R.color.default_color))
        notificationBuilder.setChannelId(packageName)

        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0,
                Intent(this, Configurations::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT))

        return notificationBuilder.build()
    }
}