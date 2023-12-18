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
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class InteractionObserver : AccessibilityService() {

    var functionsClassLegacy: FunctionsClassLegacy? = null

    var classNameCommand: String? = "Default"

    override fun onServiceConnected() {
        functionsClassLegacy = FunctionsClassLegacy(applicationContext)
    }

    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {

        functionsClassLegacy = FunctionsClassLegacy(applicationContext)

        when (accessibilityEvent.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> if (accessibilityEvent.action == 10296) {

                classNameCommand = accessibilityEvent.className as String?
                performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
                val splitIntent =
                    packageManager.getLaunchIntentForPackage(SplitTransparentPair.packageNameSplitTwo)
                splitIntent!!.addFlags(
                    Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                )
                startActivity(splitIntent)
                PublicVariable.splitScreen = true

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
