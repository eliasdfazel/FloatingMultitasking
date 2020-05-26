/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/25/20 7:03 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.LaunchPad

import android.app.Activity
import android.os.Bundle
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class OpenApplicationsLaunchPad : Activity() {
    override fun onCreate(Saved: Bundle?) {
        super.onCreate(Saved)

        val functionsClass = FunctionsClass(applicationContext)

        val packageName = intent.getStringExtra("packageName")

        try {
            if (intent.hasExtra("className")) {

                val className = intent.getStringExtra("className")

                functionsClass.openApplicationFromActivity(this@OpenApplicationsLaunchPad,
                        packageName,
                        className)

            } else {

                functionsClass
                        .openApplicationFromActivity(this@OpenApplicationsLaunchPad,
                                packageName)

            }
        } catch (e: Exception) {
            e.printStackTrace()

            functionsClass
                    .openApplicationFromActivity(this@OpenApplicationsLaunchPad,
                            packageName)
        }

        this@OpenApplicationsLaunchPad.finish()
    }
}