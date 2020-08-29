/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts

import android.content.Context
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.Utils.Functions.*

class ApplicationsViewPhoneDependencyInjection (applicationContext: Context) {

    val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    val fileIO: FileIO by lazy {
        FileIO(applicationContext)
    }

    val applicationThemeController: ApplicationThemeController by lazy {
        ApplicationThemeController(applicationContext)
    }

    val popupApplicationShortcuts: PopupApplicationShortcuts by lazy {
        PopupApplicationShortcuts(applicationContext)
    }

    val floatingServices: FloatingServices by lazy {
        FloatingServices(applicationContext)
    }

    val securityFunctions: SecurityFunctions by lazy {
        SecurityFunctions(applicationContext)
    }

    val networkCheckpoint: NetworkCheckpoint by lazy {
        NetworkCheckpoint(applicationContext)
    }

}