package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RuntimeIO (private val context: Context) {

    fun updateRecoverShortcuts() = CoroutineScope(Dispatchers.IO).launch {

        val uFile = context.getFileStreamPath(".uFile")

        if (uFile.exists()) {

            uFile.readLines().forEachIndexed { index, packageName ->

                PublicVariable.recoveryFloatingShortcuts.add(index, packageName)

            }

        }
    }

}