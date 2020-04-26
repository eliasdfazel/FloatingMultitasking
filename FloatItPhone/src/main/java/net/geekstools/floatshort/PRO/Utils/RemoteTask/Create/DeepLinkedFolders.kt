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
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons

class DeepLinkedFolders : AppCompatActivity() {

    private val htmlSymbol = "/"
    private val htmlSymbolDelete = "%20%7C%20Floating%20Category"

    override fun onCreate(Saved: Bundle?) {
        super.onCreate(Saved)
        val functionsClass = FunctionsClass(applicationContext)
        PublicVariable.floatingSizeNumber = functionsClass.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

        intent.dataString?.let { dataString ->

            val incomingURI: String = dataString

            val categoryName: String = incomingURI.substring(incomingURI.lastIndexOf(htmlSymbol) + 1)
                    .replace(htmlSymbolDelete, "")

            if (functionsClass.customIconsEnable()) {
                val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
                loadCustomIcons.load()
            }

            functionsClass.runUnlimitedFolderService(categoryName)
        }

        finish()
    }
}