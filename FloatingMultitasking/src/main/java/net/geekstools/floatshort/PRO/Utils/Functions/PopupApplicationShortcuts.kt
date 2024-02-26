/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/26/20 3:47 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.FloatIt

class PopupApplicationShortcuts (private val context: Context) {

    private val fileIO: FileIO = FileIO(context)

    private val bitmapExtractor: BitmapExtractor = BitmapExtractor(context)

    private val installedApplicationInformation: InstalledApplicationInformation = InstalledApplicationInformation(context)

    fun addPopupApplicationShortcuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {

            val shortcutManager: ShortcutManager = context.getSystemService(ShortcutManager::class.java)
            shortcutManager.removeAllDynamicShortcuts()

            val shortcutsInfo: MutableList<ShortcutInfo> = ArrayList()
            shortcutsInfo.clear()

            val intent = Intent()
            intent.setClass(context, FloatIt::class.java)

            if (context.getFileStreamPath("Frequently").exists()) {

                val appShortcuts = (fileIO.readFileLinesAsArray("Frequently"))

                if (appShortcuts != null
                    && appShortcuts.size > 3) {
                    for (i in 0..3) {

                        appShortcuts?.let {

                            intent.action = "Float_It"
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("PackageName", appShortcuts[i])
                            val shortcutInfo = ShortcutInfo.Builder(context, appShortcuts[i])
                                .setShortLabel(installedApplicationInformation.applicatioName(appShortcuts[i]))
                                .setLongLabel(installedApplicationInformation.applicatioName(appShortcuts[i]))
                                .setIcon(Icon.createWithBitmap(bitmapExtractor.getAppIconBitmapCustomIcon(appShortcuts[i])))
                                .setIntent(intent)
                                .setRank(i)
                                .build()
                            shortcutsInfo.add(shortcutInfo)

                        }

                    }
                }
            } else if (context.getFileStreamPath(".uFile").exists()) {
                if (fileIO.fileLinesCounter(".uFile") > 0) {
                    val appShortcuts = (fileIO.readFileLinesAsArray(".uFile"))
                    var countAppShortcut = 5
                    if (fileIO.fileLinesCounter(".uFile") < 5) {
                        countAppShortcut = fileIO.fileLinesCounter(".uFile")
                    }
                    for (i in 0 until countAppShortcut) {

                        appShortcuts?.let {

                            intent.action = "Float_It"
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("PackageName", appShortcuts[i])
                            val shortcutInfo = ShortcutInfo.Builder(context, appShortcuts[i])
                                    .setShortLabel(installedApplicationInformation.applicatioName(appShortcuts[i]))
                                    .setLongLabel(installedApplicationInformation.applicatioName(appShortcuts[i])) //.setIcon(Icon.createWithBitmap(appIconBitmap(appShortcuts.get(i))))
                                    .setIcon(Icon.createWithBitmap(bitmapExtractor.getAppIconBitmapCustomIcon(appShortcuts[i])))
                                    .setIntent(intent)
                                    .setRank(i)
                                    .build()
                            shortcutsInfo.add(shortcutInfo)

                        }

                    }
                }
            }

            shortcutManager.addDynamicShortcuts(shortcutsInfo)

        }
    }

}