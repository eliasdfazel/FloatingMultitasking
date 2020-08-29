/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons

class CreateFloatingShortcuts : AppCompatActivity() {

    override fun onCreate(Saved: Bundle?) {
        super.onCreate(Saved)

        if (intent.hasExtra("PackageName")
                && intent.hasExtra("ClassName")) {

            val functionsClass = FunctionsClassLegacy(applicationContext)

            val packageName = intent.getStringExtra("PackageName")
            val className = intent.getStringExtra("ClassName")

            if (functionsClass.customIconsEnable()) {
                val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
                loadCustomIcons.load()
            }

            functionsClass.runUnlimitedShortcutsServiceHIS(packageName, className)        }

        finish()
    }
}