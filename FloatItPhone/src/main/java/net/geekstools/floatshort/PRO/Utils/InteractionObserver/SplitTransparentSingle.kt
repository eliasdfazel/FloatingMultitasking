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
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class SplitTransparentSingle : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
        getWindow().navigationBarColor = Color.TRANSPARENT

        val accessibilityManager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager

        val accessibilityEvent = AccessibilityEvent.obtain()
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
