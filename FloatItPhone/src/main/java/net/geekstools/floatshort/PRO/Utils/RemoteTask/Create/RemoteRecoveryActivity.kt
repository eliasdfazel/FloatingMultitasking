/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/24/20 8:00 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassRunServices
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons

class RemoteRecoveryActivity : Activity() {
    override fun onCreate(Saved: Bundle?) {
        super.onCreate(Saved)

        intent.getStringExtra("packageName")?.let { packageName ->

            val functionsClass = FunctionsClass(applicationContext)
            val functionsClassRunServices = FunctionsClassRunServices(applicationContext)

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