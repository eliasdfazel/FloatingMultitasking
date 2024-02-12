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

import net.geekstools.floatshort.PRO.Folders.FloatingServices.FloatingFolders
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class FloatingFoldersUtils {

    companion object FloatingFoldersCounterType {
        val floatingFoldersCounterType: HashMap<String, Int> = HashMap<String, Int>()
    }

    init {
        floatingFoldersCounterType[FloatingFolders::class.java.simpleName] = PublicVariable.floatingFolderCounter_Folder
    }

    fun floatingFoldersCounterType(floatingFoldersClassName: String) {

        when (floatingFoldersClassName) {
            FloatingFolders::class.java.simpleName -> {

                PublicVariable.floatingFolderCounter_Folder = PublicVariable.floatingFolderCounter_Folder - 1

            }
            else -> {
                PublicVariable.floatingFolderCounter_Folder = PublicVariable.floatingFolderCounter_Folder - 1
            }
        }
    }
}