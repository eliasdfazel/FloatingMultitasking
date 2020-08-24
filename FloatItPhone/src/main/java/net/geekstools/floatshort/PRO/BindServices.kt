/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 6:17 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.nfc.NfcManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Automation.RecoveryServices.RecoveryGps
import net.geekstools.floatshort.PRO.Automation.RecoveryServices.RecoveryNfc
import net.geekstools.floatshort.PRO.Automation.RecoveryServices.RecoveryWifi
import net.geekstools.floatshort.PRO.Folders.FloatingServices.FloatingFoldersForGps
import net.geekstools.floatshort.PRO.Folders.FloatingServices.FloatingFoldersForNfc
import net.geekstools.floatshort.PRO.Folders.FloatingServices.FloatingFoldersForWifi
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForGps
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForNfc
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForWifi
import net.geekstools.floatshort.PRO.Utils.Functions.Debug
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class BindServices : Service() {

    lateinit var functionsClass: FunctionsClass
    lateinit var fileIO: FileIO

    var broadcastReceiverAction: BroadcastReceiver? = null

    private companion object {
        var triggerWifiBroadcast = false
    }

    override fun onDestroy() {
        super.onDestroy()

        broadcastReceiverAction?.let {
            unregisterReceiver(it)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Debug.PrintDebug("*** Bind Service StartId $startId ***")

        if (startId == 1) {
            startForeground(333, functionsClass.bindServiceNotification())

            PublicVariable.floatingSizeNumber = functionsClass.readDefaultPreference("floatingSize", 39)
            PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), applicationContext.resources.displayMetrics).toInt()

            if (functionsClass.returnAPI() >= Build.VERSION_CODES.O
                    && fileIO.automationFeatureEnable()) {

                val intentFilter = IntentFilter()
                intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
                intentFilter.addAction("android.location.PROVIDERS_CHANGED")
                intentFilter.addAction("android.nfc.action.ADAPTER_STATE_CHANGED")
                intentFilter.addAction("REMOVE_SELF")
                broadcastReceiverAction = object : BroadcastReceiver() {

                    override fun onReceive(context: Context?, intent: Intent?) {
                        intent?.let {
                            if (context != null) {
                                if (intent.action == "android.net.wifi.WIFI_STATE_CHANGED" && triggerWifiBroadcast) {

                                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?

                                    wifiManager?.let {
                                        if (wifiManager.isWifiEnabled
                                                && !PublicVariable.receiveWiFi) {

                                            if (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED) {
                                                val wifiRecovery = Intent(context, RecoveryWifi::class.java)
                                                wifiRecovery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startService(wifiRecovery)
                                                PublicVariable.receiveWiFi = true
                                            }
                                        } else if (!wifiManager.isWifiEnabled) {

                                            if (wifiManager.wifiState == WifiManager.WIFI_STATE_DISABLED) {
                                                val wifiShortcutsRemove = Intent(context, FloatingShortcutsForWifi::class.java)
                                                wifiShortcutsRemove.putExtra(context.getString(R.string.remove_all_floatings), context.getString(R.string.remove_all_floatings))
                                                wifiShortcutsRemove.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                                context.startService(wifiShortcutsRemove)

                                                val wifiCategoryRemove = Intent(context, FloatingFoldersForWifi::class.java)
                                                wifiCategoryRemove.putExtra(context.getString(R.string.remove_all_floatings), context.getString(R.string.remove_all_floatings))
                                                wifiCategoryRemove.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                                context.startService(wifiCategoryRemove)

                                                PublicVariable.receiveWiFi = false
                                            }
                                        }
                                    }
                                } else if (intent.action == "android.location.PROVIDERS_CHANGED") {

                                    val locationManager = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

                                    locationManager?.let {
                                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                                                && !PublicVariable.receiverGPS) {

                                            val gpsRecovery = Intent(context, RecoveryGps::class.java)
                                            gpsRecovery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startService(gpsRecovery)

                                            PublicVariable.receiverGPS = true

                                        } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                            val gpsShortcutsRemove = Intent(context, FloatingShortcutsForGps::class.java)
                                            gpsShortcutsRemove.putExtra(context.getString(R.string.remove_all_floatings), context.getString(R.string.remove_all_floatings))
                                            gpsShortcutsRemove.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            context.startService(gpsShortcutsRemove)

                                            val gpsCategoryRemove = Intent(context, FloatingFoldersForGps::class.java)
                                            gpsCategoryRemove.putExtra(context.getString(R.string.remove_all_floatings), context.getString(R.string.remove_all_floatings))
                                            gpsCategoryRemove.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            context.startService(gpsCategoryRemove)

                                            PublicVariable.receiverGPS = false
                                        }
                                    }
                                } else if (intent.action == "android.nfc.action.ADAPTER_STATE_CHANGED") {

                                    val nfcManager = context.applicationContext.getSystemService(Context.NFC_SERVICE) as NfcManager?

                                    nfcManager?.let {
                                        if (nfcManager.defaultAdapter.isEnabled && !PublicVariable.receiverNFC) {
                                            val nfcRecovery = Intent(context, RecoveryNfc::class.java)
                                            nfcRecovery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startService(nfcRecovery)

                                            PublicVariable.receiverNFC = true

                                        } else if (!nfcManager.defaultAdapter.isEnabled) {
                                            val nfcShortcutsRemove = Intent(context, FloatingShortcutsForNfc::class.java)
                                            nfcShortcutsRemove.putExtra(context.getString(R.string.remove_all_floatings), context.getString(R.string.remove_all_floatings))
                                            nfcShortcutsRemove.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            context.startService(nfcShortcutsRemove)

                                            val nfcFolderRemove = Intent(context, FloatingFoldersForNfc::class.java)
                                            nfcFolderRemove.putExtra(context.getString(R.string.remove_all_floatings), context.getString(R.string.remove_all_floatings))
                                            nfcFolderRemove.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            context.startService(nfcFolderRemove)

                                            PublicVariable.receiverNFC = false
                                        }
                                    }
                                } else if (intent.action == "REMOVE_SELF") {
                                    stopForeground(true)
                                    stopSelf()
                                }
                            }
                        }
                    }
                }
                registerReceiver(broadcastReceiverAction, intentFilter)

                Handler().postDelayed({
                    triggerWifiBroadcast = true
                }, 3000)
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        functionsClass = FunctionsClass(applicationContext)
        fileIO = FileIO(applicationContext)

        functionsClass.loadSavedColor()
        functionsClass.checkLightDarkTheme()
    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }
}