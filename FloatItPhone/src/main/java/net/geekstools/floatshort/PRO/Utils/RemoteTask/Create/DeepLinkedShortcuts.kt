/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/26/20 5:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassRunServices
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons

class DeepLinkedShortcuts : AppCompatActivity() {

    private val htmlSymbol = "20%7C%20"
    private val htmlSymbolDelete = "0%7C%20"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val functionsClass: FunctionsClass = FunctionsClass(applicationContext)
        val functionsClassRunServices: FunctionsClassRunServices = FunctionsClassRunServices(applicationContext)

        PublicVariable.floatingSizeNumber = functionsClass.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

        intent.dataString?.let { dataString ->

            val incomingURI: String = dataString
            val packageName: String = incomingURI.substring(incomingURI.lastIndexOf(htmlSymbol) + 1)
                    .replace(htmlSymbolDelete, "")

            if (functionsClass.customIconsEnable()) {
                val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
                loadCustomIcons.load()
            }

            functionsClassRunServices.runUnlimitedShortcutsServicePackage(packageName)
        }

        finish()
    }
}