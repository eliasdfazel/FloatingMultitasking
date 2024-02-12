/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 6:13 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import java.io.File
import java.nio.charset.Charset

class FileIO(private val context: Context) {

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