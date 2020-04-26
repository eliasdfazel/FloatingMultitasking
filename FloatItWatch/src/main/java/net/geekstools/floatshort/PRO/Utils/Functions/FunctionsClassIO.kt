/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/25/20 6:07 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.os.Build
import java.io.File
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

        return File(fileName).readLines(Charset.defaultCharset()).size
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
                fileLinesCounter(".autoBluetoothCategory`")
            } else {
                0
            }

            //.autoGpsCategory
            val autoGpsCategory = context.getFileStreamPath(".autoGpsCategory")
            val autoGpsCategorySize = if (autoGpsCategory.exists()) {
                fileLinesCounter(".autoGpsCategory`")
            } else {
                0
            }

            //.autoNfcCategory
            val autoNfcCategory = context.getFileStreamPath(".autoNfcCategory")
            val autoNfcCategorySize = if (autoNfcCategory.exists()) {
                fileLinesCounter(".autoNfcCategory`")
            } else {
                0
            }

            //.autoWifiCategory
            val autoWifiCategory = context.getFileStreamPath(".autoWifiCategory")
            val autoWifiCategorySize = if (autoWifiCategory.exists()) {
                fileLinesCounter(".autoWifiCategory`")
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
}