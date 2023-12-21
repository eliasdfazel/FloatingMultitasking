package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RuntimeIO (private val context: Context, private val functionsClassLegacy: FunctionsClassLegacy) {

    fun updateRecoverShortcuts() = CoroutineScope(Dispatchers.IO).launch {
        Log.d(this@RuntimeIO.javaClass.simpleName, "Update Recovery Shortcuts")

        val uFile = context.getFileStreamPath(".uFile")

        if (uFile.exists()) {

            uFile.readLines().forEachIndexed { index, packageName ->

                PublicVariable.recoveryFloatingShortcuts.add(index, packageName)

            }

        }
    }

    fun freeformCheckpoint() {
        Log.d(this@RuntimeIO.javaClass.simpleName, "FreeForm Support: ${functionsClassLegacy.freeFormSupport(context)}")

        if (functionsClassLegacy.freeFormSupport(context)) {

            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean("freeForm", true)
                .apply()

        }

    }

}