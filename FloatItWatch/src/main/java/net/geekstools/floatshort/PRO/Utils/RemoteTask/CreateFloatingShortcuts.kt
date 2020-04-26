/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/26/20 7:46 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.RemoteTask

import android.app.Activity
import android.os.Bundle
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class CreateFloatingShortcuts : Activity() {

    override fun onCreate(Saved: Bundle?) {
        super.onCreate(Saved)

        if (intent.hasExtra("PackageName")
                && intent.hasExtra("ClassName")) {

            val functionsClass = FunctionsClass(applicationContext)

            val packageName = intent.getStringExtra("PackageName")
            val className = intent.getStringExtra("ClassName")

            functionsClass.runUnlimitedShortcutsServiceHIS(packageName, className)
        }

        this@CreateFloatingShortcuts.finish()
    }
}