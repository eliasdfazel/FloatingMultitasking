/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/15/20 2:27 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FloatingServices.Utils

import net.geekstools.floatshort.PRO.Folders.FloatingServices.*
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class FloatingFoldersUtils {

    companion object FloatingFoldersCounterType {
        val floatingFoldersCounterType: HashMap<String, Int> = HashMap<String, Int>()
    }

    init {
        floatingFoldersCounterType[FloatingFolders::class.java.simpleName] = PublicVariable.floatingFolderCounter_Folder
        floatingFoldersCounterType[FloatingFoldersForBluetooth::class.java.simpleName] = PublicVariable.floatingFolderCounter_Bluetooth
        floatingFoldersCounterType[FloatingFoldersForGps::class.java.simpleName] = PublicVariable.floatingFolderCounter_Gps
        floatingFoldersCounterType[FloatingFoldersForNfc::class.java.simpleName] = PublicVariable.floatingFolderCounter_Nfc
        floatingFoldersCounterType[FloatingFoldersForTime::class.java.simpleName] = PublicVariable.floatingFolderCounter_Time
        floatingFoldersCounterType[FloatingFoldersForWifi::class.java.simpleName] = PublicVariable.floatingFolderCounter_Wifi
    }

    fun floatingFoldersCounterType(floatingFoldersClassName: String) {

        when (floatingFoldersClassName) {
            FloatingFolders::class.java.simpleName -> {

                PublicVariable.floatingFolderCounter_Folder = PublicVariable.floatingFolderCounter_Folder - 1

            }
            FloatingFoldersForBluetooth::class.java.simpleName -> {

                PublicVariable.floatingFolderCounter_Bluetooth = PublicVariable.floatingFolderCounter_Bluetooth - 1

            }
            FloatingFoldersForGps::class.java.simpleName -> {

                PublicVariable.floatingFolderCounter_Gps = PublicVariable.floatingFolderCounter_Gps - 1

            }
            FloatingFoldersForNfc::class.java.simpleName -> {

                PublicVariable.floatingFolderCounter_Nfc = PublicVariable.floatingFolderCounter_Nfc - 1

            }
            FloatingFoldersForTime::class.java.simpleName -> {

                PublicVariable.floatingFolderCounter_Time = PublicVariable.floatingFolderCounter_Time - 1

            }
            FloatingFoldersForWifi::class.java.simpleName -> {

                PublicVariable.floatingFolderCounter_Wifi = PublicVariable.floatingFolderCounter_Wifi - 1

            }
            else -> {
                PublicVariable.floatingFolderCounter_Folder = PublicVariable.floatingFolderCounter_Folder - 1
            }
        }
    }
}