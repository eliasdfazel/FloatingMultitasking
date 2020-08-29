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
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons

class DeepLinkedShortcuts : AppCompatActivity() {

    private val htmlSymbol = "20%7C%20"
    private val htmlSymbolDelete = "0%7C%20"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(applicationContext)
        val floatingServices: FloatingServices = FloatingServices(applicationContext)

        PublicVariable.floatingSizeNumber = functionsClassLegacy.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

        intent.dataString?.let { dataString ->

            val incomingURI: String = dataString
            val packageName: String = incomingURI.substring(incomingURI.lastIndexOf(htmlSymbol) + 1)
                    .replace(htmlSymbolDelete, "")

            if (functionsClassLegacy.customIconsEnable()) {
                val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClassLegacy.customIconPackageName())
                loadCustomIcons.load()
            }

            floatingServices.runUnlimitedShortcutsServicePackage(packageName)
        }

        finish()
    }
}