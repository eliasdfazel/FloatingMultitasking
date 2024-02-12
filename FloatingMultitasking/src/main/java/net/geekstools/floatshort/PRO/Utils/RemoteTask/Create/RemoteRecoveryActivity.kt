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

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons

class RemoteRecoveryActivity : Activity() {
    override fun onCreate(Saved: Bundle?) {
        super.onCreate(Saved)

        intent.getStringExtra("packageName")?.let { packageName ->

            val functionsClass = FunctionsClassLegacy(applicationContext)
            val functionsClassRunServices = FloatingServices(applicationContext)

            PublicVariable.floatingSizeNumber = functionsClass.readDefaultPreference("floatingSize", 39)
            PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

            if (functionsClass.customIconsEnable()) {
                val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
                loadCustomIcons.load()
            }

            functionsClassRunServices.runUnlimitedShortcutsServicePackage(packageName)

        }

        this@RemoteRecoveryActivity.finish()
    }
}