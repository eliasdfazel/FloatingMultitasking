/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 6:37 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders

import android.content.Context
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.Utils.Functions.ApplicationThemeController
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.NetworkCheckpoint
import net.geekstools.floatshort.PRO.Utils.Functions.PopupApplicationShortcuts
import net.geekstools.floatshort.PRO.Utils.Functions.PreferencesIO

class FoldersConfigurationsDependencyInjection (applicationContext: Context) {

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

    val preferencesIO: PreferencesIO by lazy {
        PreferencesIO(applicationContext)
    }

}