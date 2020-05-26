/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/25/20 7:04 PM
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

        val appPackageName: String? = intent.getStringExtra("packageName")

        appPackageName?.let {

            try {
                if (intent.hasExtra("className")) {

                    val appClassName = intent.getStringExtra("className")

                    functionsClass.openApplicationFromActivity(this@OpenApplicationsLaunchPad,
                            appPackageName,
                            appClassName)

                } else {

                    functionsClass
                            .openApplicationFromActivity(this@OpenApplicationsLaunchPad,
                                    appPackageName)

                }
            } catch (e: Exception) {
                e.printStackTrace()

                functionsClass
                        .openApplicationFromActivity(this@OpenApplicationsLaunchPad,
                                appPackageName)
            }
        }

        this@OpenApplicationsLaunchPad.finish()
    }
}