/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/19/20 8:14 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PreferencesIO
import net.geekstools.floatshort.PRO.Utils.RemoteTask.BootRecovery
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryFolders
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryShortcuts
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons

class BootRecoveryReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null) {

            val functionsClassLegacy = FunctionsClassLegacy(context)
            val preferencesIO = PreferencesIO(context)
            val fileIO = FileIO(context)

            preferencesIO.savePreference("WidgetsInformation", "Reallocated", false)

            if (intent!!.action == Intent.ACTION_BOOT_COMPLETED) {

                if (functionsClassLegacy.ControlPanel() || fileIO.automationFeatureEnable()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(Intent(context, BindServices::class.java))
                    } else {
                        context.startService(Intent(context, BindServices::class.java))
                    }
                }

                if (functionsClassLegacy.customIconsEnable()) {
                    val loadCustomIcons = LoadCustomIcons(context, functionsClassLegacy.customIconPackageName())
                    loadCustomIcons.load()
                }



                when (PreferenceManager.getDefaultSharedPreferences(context).getString("boot", "1")) {
                    BootRecovery.Mode.NONE -> {



                    }
                    BootRecovery.Mode.SHORTCUTS -> {

                        Handler(Looper.getMainLooper()).postDelayed({

                            val shortcutsRecovery = Intent(context, RecoveryShortcuts::class.java)
                            shortcutsRecovery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(shortcutsRecovery)
                            } else {
                                context.startService(shortcutsRecovery)
                            }

                        }, 1000)

                    }
                    BootRecovery.Mode.FOLDERS -> {

                        Handler(Looper.getMainLooper()).postDelayed({

                            val categoryRecovery = Intent(context, RecoveryFolders::class.java)
                            categoryRecovery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(categoryRecovery)
                            } else {
                                context.startService(categoryRecovery)
                            }

                        }, 2000)

                    }
                    BootRecovery.Mode.RECOVER_ALL -> {

                        Handler(Looper.getMainLooper()).postDelayed({

                            val shortcutsRecovery = Intent(context, RecoveryShortcuts::class.java)
                            shortcutsRecovery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(shortcutsRecovery)
                            } else {
                                context.startService(shortcutsRecovery)
                            }

                        }, 1000)

                        Handler(Looper.getMainLooper()).postDelayed({

                            val categoryRecovery = Intent(context, RecoveryFolders::class.java)
                            categoryRecovery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(categoryRecovery)
                            } else {
                                context.startService(categoryRecovery)
                            }

                        }, 3000)

                    }
                }

            }

        }
    }

}