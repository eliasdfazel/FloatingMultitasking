/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/20/20 5:05 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.os.Build
import java.io.File
import java.nio.charset.Charset

class FunctionsClassIO(private val context: Context) {

    fun readFileLinesAsArray(fileName: String) : Array<String>? {
        val file: File? = context.getFileStreamPath(fileName)

        return if (file != null) {
            if (file.exists() && file.isFile) {
                file.readLines(Charset.defaultCharset()).toTypedArray()
            } else {
                null
            }
        } else {
            null
        }
    }

    fun readFileLinesAsList(fileName: String) : ArrayList<String>? {
        val file: File? = context.getFileStreamPath(fileName)

        return (if (file != null) {
            if (file.exists() && file.isFile) {
                file.readLines(Charset.defaultCharset())
            } else {
                null
            }
        } else {
            null
        }) as ArrayList<String>?
    }

    fun fileLinesCounter(fileName: String) : Int {
        val file: File? = context.getFileStreamPath(fileName)

        return if (file != null) {
            if (file.exists() && file.isFile) {
                file.readLines(Charset.defaultCharset()).size
            } else {
                0
            }
        } else {
            0
        }
    }

    fun readFile(fileName: String) : String? {
        val file: File? = context.getFileStreamPath(fileName)

        return if (file != null) {
            if (file.exists() && file.isFile) {
                file.readText(Charset.defaultCharset())
            } else {
                null
            }
        } else {
            null
        }
    }

    fun automationFeatureEnable(): Boolean {
        var automationEnabled = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //.autoBluetooth
            val autoBluetooth = context.getFileStreamPath(".autoBluetooth")
            val autoBluetoothSize = if (autoBluetooth.exists()) {
                fileLinesCounter(".autoBluetooth")
            } else {
                0
            }

            //.autoGps
            val autoGps = context.getFileStreamPath(".autoGps")
            val autoGpsSize = if (autoGps.exists()) {
                fileLinesCounter(".autoGps")
            } else {
                0
            }

            //.autoNfc
            val autoNfc = context.getFileStreamPath(".autoNfc")
            val autoNfcSize = if (autoNfc.exists()) {
                fileLinesCounter(".autoNfc")
            } else {
                0
            }

            //.autoWifi
            val autoWifi = context.getFileStreamPath(".autoWifi")
            val autoWifiSize = if (autoWifi.exists()) {
                fileLinesCounter(".autoWifi")
            } else {
                0
            }

            //.autoBluetoothCategory
            val autoBluetoothCategory = context.getFileStreamPath(".autoBluetoothCategory")
            val autoBluetoothCategorySize = if (autoBluetoothCategory.exists()) {
                fileLinesCounter(".autoBluetoothCategory")
            } else {
                0
            }

            //.autoGpsCategory
            val autoGpsCategory = context.getFileStreamPath(".autoGpsCategory")
            val autoGpsCategorySize = if (autoGpsCategory.exists()) {
                fileLinesCounter(".autoGpsCategory")
            } else {
                0
            }

            //.autoNfcCategory
            val autoNfcCategory = context.getFileStreamPath(".autoNfcCategory")
            val autoNfcCategorySize = if (autoNfcCategory.exists()) {
                fileLinesCounter(".autoNfcCategory")
            } else {
                0
            }

            //.autoWifiCategory
            val autoWifiCategory = context.getFileStreamPath(".autoWifiCategory")
            val autoWifiCategorySize = if (autoWifiCategory.exists()) {
                fileLinesCounter(".autoWifiCategory")
            } else {
                0
            }

            val autoCounterSum = autoBluetoothSize + autoGpsSize + autoNfcSize + autoWifiSize + autoBluetoothCategorySize + autoGpsCategorySize + autoNfcCategorySize + autoWifiCategorySize
            if (autoCounterSum > 0) {

                automationEnabled = true
            }
        }

        return automationEnabled
    }

    fun saveFile(fileName: String, content: String) {
        try {

            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(content.toByteArray())

            fileOutputStream.flush()
            fileOutputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveFileEmpty(fileName: String?) {

        val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)

        fileOutputStream.flush()
        fileOutputStream.close()

    }


    fun saveFileAppendLine(fileName: String, content: String) {

        val fileOutputStream = context.openFileOutput(fileName, Context.MODE_APPEND)

        fileOutputStream.write("${content}\n".toByteArray())

        fileOutputStream.flush()
        fileOutputStream.close()

    }


    fun removeLine(fileName: String, lineToRemove: String) {

        val temporaryFileName = "${fileName}.Temporary"

        readFileLinesAsList(fileName)?.forEach {

            if (it != lineToRemove) {

                saveFileAppendLine(temporaryFileName, it)

            }

        }

        context.deleteFile(fileName)
        context.getFileStreamPath(temporaryFileName).renameTo(context.getFileStreamPath(fileName))

    }

}