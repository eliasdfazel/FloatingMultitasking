/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 1:38 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import java.nio.charset.Charset

class FunctionsClassIO (private val context: Context) {

    fun readFileLines(fileName: String) : Array<String>? {
        val file = context.getFileStreamPath(fileName)

        return if (file.exists()) {

            file.readLines(Charset.defaultCharset()).toTypedArray()
        } else {
            null
        }
    }

    fun fileLinesCounter(fileName: String) : Int {
        val file = context.getFileStreamPath(fileName)

        return if (file.exists()) {
            file.readLines(Charset.defaultCharset()).size
        } else {
            0
        }
    }
}