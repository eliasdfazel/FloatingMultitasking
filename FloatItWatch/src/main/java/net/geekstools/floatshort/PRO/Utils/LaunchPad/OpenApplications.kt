/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/26/20 8:26 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.LaunchPad

import android.app.Activity
import android.os.Bundle
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class OpenApplications : Activity() {

    override fun onCreate(Saved: Bundle?) {
        super.onCreate(Saved)

        val functionsClass = FunctionsClass(applicationContext)

        val packageName = intent.getStringExtra("packageName")

        if (intent.hasExtra("className")) {
            val className = intent.getStringExtra("className")

            functionsClass.openApplication(packageName, className)

        } else {

            functionsClass.openApplication(packageName)
        }
        try {

        } catch (e: Exception) {
            e.printStackTrace()

            functionsClass.openApplication(packageName)
        }

        this@OpenApplications.finish()
    }
}