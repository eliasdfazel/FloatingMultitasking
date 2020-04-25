/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/25/20 5:43 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import java.io.File
import java.nio.charset.Charset
import java.util.*

class FunctionsClassIO (private val context: Context) {

    fun readFileLines(fileName: String) : Array<String>? {
        val file = context.getFileStreamPath(fileName)

        return if (file.exists()) {

            file.readLines(Charset.defaultCharset()).toTypedArray()
        } else {
            null
        }
    }

    fun automationFeatureEnable(): Boolean {
        var automationEnabled = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val autoFileName: ArrayList<String> = ArrayList<String>()
            val filesList = context.getFileStreamPath("").listFiles()

            if (!filesList.isNullOrEmpty()) {

                for (aFile in filesList) {
                    FunctionsClassDebug.PrintDebug("*** Automation Enabled == " + aFile.absolutePath)

                    if (aFile.name.contains(".auto")) {
                        FunctionsClassDebug.PrintDebug("*** Automation File Found == " + aFile.absolutePath)

                        autoFileName.add(aFile.name)
                    }
                }

                var totalCount = 0
                for (fileName in autoFileName) {
                    val countLine: Int = File(fileName).readLines(Charset.defaultCharset()).size
                    totalCount += countLine
                }

                if (totalCount > 0) {
                    automationEnabled = true

                    PreferenceManager.getDefaultSharedPreferences(context)
                            .edit().apply {
                                putBoolean("stable", true)
                                apply()
                            }
                }
            }
        }

        return automationEnabled
    }
}