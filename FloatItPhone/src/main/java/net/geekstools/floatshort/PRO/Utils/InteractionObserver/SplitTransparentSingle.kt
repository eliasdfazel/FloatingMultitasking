/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.InteractionObserver

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

@Suppress("DEPRECATION")
class SplitTransparentSingle : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = getColor(R.color.dark)
        window.navigationBarColor = getColor(R.color.dark)

        val accessibilityManager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager

        val accessibilityEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AccessibilityEvent()
        } else {
            AccessibilityEvent.obtain()
        }
        accessibilityEvent.setSource(Button(applicationContext))
        accessibilityEvent.eventType = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        accessibilityEvent.action = 69201
        accessibilityEvent.className = SplitTransparentPair::class.java.simpleName
        accessibilityEvent.text.add(packageName)

        accessibilityManager.sendAccessibilityEvent(accessibilityEvent)

        var splitSingle: Intent? = Intent()
        if (PublicVariable.splitSingleClassName != null) {
            splitSingle?.setClassName(PublicVariable.splitSinglePackage, PublicVariable.splitSingleClassName)
        } else {
            splitSingle = packageManager.getLaunchIntentForPackage(PublicVariable.splitSinglePackage)
        }
        splitSingle?.flags = Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(splitSingle)

    }

}
