/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 6:46 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widgets

import android.content.Context
import net.geekstools.floatshort.PRO.Utils.Functions.ApplicationThemeController
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy

class WidgetConfigurationsDependencyInjection (applicationContext: Context) {

    val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    val fileIO: FileIO by lazy {
        FileIO(applicationContext)
    }

    val applicationThemeController: ApplicationThemeController by lazy {
        ApplicationThemeController(applicationContext)
    }

    val floatingServices: FloatingServices by lazy {
        FloatingServices(applicationContext)
    }

}