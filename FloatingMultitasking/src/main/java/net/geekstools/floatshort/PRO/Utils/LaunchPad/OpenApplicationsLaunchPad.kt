/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.LaunchPad

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy

class OpenApplicationsLaunchPad : AppCompatActivity() {

    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)

        val functionsClass = FunctionsClassLegacy(applicationContext)

        val appPackageName: String? = intent.getStringExtra("PackageName")

        appPackageName?.let {

            try {
                if (intent.hasExtra("ClassName")) {

                    val appClassName = intent.getStringExtra("ClassName")

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