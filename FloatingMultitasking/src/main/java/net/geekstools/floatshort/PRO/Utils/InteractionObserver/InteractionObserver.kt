/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.InteractionObserver

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class InteractionObserver : AccessibilityService() {

    private var classNameCommand: String? = "Default"

    override fun onServiceConnected() {}

    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {

        when (accessibilityEvent.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> if (accessibilityEvent.action == 10296) {

                val splitIntent = packageManager.getLaunchIntentForPackage(SplitTransparentPair.packageNameSplitTwo)

                splitIntent?.let {

                    classNameCommand = accessibilityEvent.className as String?

                    performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)

                    splitIntent.addFlags(
                        Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    startActivity(splitIntent)

                    PublicVariable.splitScreen = true

                }

            } else if (accessibilityEvent.action == 69201) {

                classNameCommand = accessibilityEvent.className as String?

                performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)

            } else if (accessibilityEvent.action == 66666) {

                performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)

            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onInterrupt() {

        startService(Intent(applicationContext, InteractionObserver::class.java))

    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
