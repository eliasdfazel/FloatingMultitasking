/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 7:18 AM
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
import net.geekstools.floatshort.PRO.Automation.RecoveryShortcuts
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class BootRecoverReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        intent?.let {
            if (context != null) {
                val functionsClass = FunctionsClass(context)

                if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                    if (functionsClass.bootReceiverEnabled()) {

                        Intent(context, BindServices::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(this@apply)
                            } else {
                                context.startService(this@apply)
                            }
                        }

                        if (functionsClass.bootReceiverEnabled()) {

                            Handler().postDelayed({
                                Intent(context, RecoveryShortcuts::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(this@apply)
                                    } else {
                                        context.startService(this@apply)
                                    }
                                }
                            }, 3000)

                        } else {

                            context.stopService(Intent(context, BindServices::class.java))
                        }
                    }
                }
            }
        }
    }
}