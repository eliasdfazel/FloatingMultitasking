/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/25/20 5:17 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.R
import java.util.*

class PopupApplicationShortcuts (private val context: Context) {

    private val fileIO: FileIO = FileIO(context)

    private val bitmapExtractor: BitmapExtractor = BitmapExtractor(context)

    private val shapingBitmap: ShapingBitmap = ShapingBitmap(context)

    private val installedApplicationInformation: InstalledApplicationInformation = InstalledApplicationInformation(context)

    fun addPopupApplicationShortcuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {

            val shortcutManager: ShortcutManager = context.getSystemService(ShortcutManager::class.java)
            shortcutManager.removeAllDynamicShortcuts()

            val shortcutInfos: MutableList<ShortcutInfo> = ArrayList()
            shortcutInfos.clear()

            val intent = Intent()

            if (context.getFileStreamPath("Frequently").exists()) {

                val appShortcuts = (fileIO.readFileLinesAsArray("Frequently"))

                for (i in 0..3) {

                    appShortcuts?.let {

                        intent.action = "Remote_Single_Floating_Shortcuts"
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("packageName", appShortcuts[i])
                        val shortcutInfo = ShortcutInfo.Builder(context, appShortcuts[i])
                                .setShortLabel(installedApplicationInformation.applicatioName(appShortcuts[i]))
                                .setLongLabel(installedApplicationInformation.applicatioName(appShortcuts[i]))
                                .setIcon(Icon.createWithBitmap(bitmapExtractor.getAppIconBitmapCustomIcon(appShortcuts[i])))
                                .setIntent(intent)
                                .setRank(i)
                                .build()
                        shortcutInfos.add(shortcutInfo)

                    }

                }
            } else if (context.getFileStreamPath(".uFile").exists()) {
                if (fileIO.fileLinesCounter(".uFile") > 0) {
                    val appShortcuts = (fileIO.readFileLinesAsArray(".uFile"))
                    var countAppShortcut = 4
                    if (fileIO.fileLinesCounter(".uFile") < 4) {
                        countAppShortcut = fileIO.fileLinesCounter(".uFile")
                    }
                    for (i in 0 until countAppShortcut) {

                        appShortcuts?.let {

                            intent.action = "Remote_Single_Floating_Shortcuts"
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("packageName", appShortcuts[i])
                            val shortcutInfo = ShortcutInfo.Builder(context, appShortcuts[i])
                                    .setShortLabel(installedApplicationInformation.applicatioName(appShortcuts[i]))
                                    .setLongLabel(installedApplicationInformation.applicatioName(appShortcuts[i])) //.setIcon(Icon.createWithBitmap(appIconBitmap(appShortcuts.get(i))))
                                    .setIcon(Icon.createWithBitmap(bitmapExtractor.getAppIconBitmapCustomIcon(appShortcuts[i])))
                                    .setIntent(intent)
                                    .setRank(i)
                                    .build()
                            shortcutInfos.add(shortcutInfo)

                        }

                    }
                }
            }

            try {
                var dynamicLabel: String? = null
                if (PreferenceManager.getDefaultSharedPreferences(context).getString("boot", "1") == "0") {
                    shortcutManager.addDynamicShortcuts(shortcutInfos)
                    return
                } else if (PreferenceManager.getDefaultSharedPreferences(context).getString("boot", "1") == "1") {
                    intent.action = "Remote_Recover_Shortcuts"
                    dynamicLabel = context.getString(R.string.shortcuts)
                } else if (PreferenceManager.getDefaultSharedPreferences(context).getString("boot", "1") == "2") {
                    intent.action = "Remote_Recover_Categories"
                    dynamicLabel = context.getString(R.string.floatingFolders)
                } else if (PreferenceManager.getDefaultSharedPreferences(context).getString("boot", "1") == "3") {
                    intent.action = "Remote_Recover_All"
                    dynamicLabel = context.getString(R.string.recover_all)
                }
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val drawCategory = context.getDrawable(R.drawable.draw_recovery_popup) as LayerDrawable
                var shapeTempDrawable = shapingBitmap.shapesDrawables()
                if (shapeTempDrawable != null) {
                    val frontDrawable: Drawable? = context.getDrawable(R.drawable.w_recovery_popup)?.mutate()
                    frontDrawable?.setTint(Color.WHITE)
                    shapeTempDrawable.setTint(PublicVariable.primaryColor)
                    drawCategory.setDrawableByLayerId(R.id.backgroundTemporary, shapeTempDrawable)
                    drawCategory.setDrawableByLayerId(R.id.fronttemp, frontDrawable)
                } else {
                    shapeTempDrawable = ColorDrawable(Color.TRANSPARENT)
                    val frontDrawable: Drawable? = context.getDrawable(R.drawable.w_recovery_popup)?.mutate()
                    frontDrawable?.setTint(context.getColor(R.color.default_color))
                    shapeTempDrawable.setTint(PublicVariable.primaryColor)
                    drawCategory.setDrawableByLayerId(R.id.backgroundTemporary, shapeTempDrawable)
                    drawCategory.setDrawableByLayerId(R.id.fronttemp, frontDrawable)
                }
                val recoveryBitmap = Bitmap
                        .createBitmap(drawCategory.intrinsicWidth, drawCategory.intrinsicHeight, Bitmap.Config.ARGB_8888)
                drawCategory.setBounds(0, 0, drawCategory.intrinsicWidth, drawCategory.intrinsicHeight)
                drawCategory.draw(Canvas(recoveryBitmap))
                val shortcutInfo = ShortcutInfo.Builder(context, dynamicLabel)
                        .setShortLabel(dynamicLabel!!)
                        .setLongLabel(dynamicLabel)
                        .setIcon(Icon.createWithBitmap(recoveryBitmap))
                        .setIntent(intent)
                        .setRank(5)
                        .build()
                shortcutInfos.add(shortcutInfo)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            shortcutManager.addDynamicShortcuts(shortcutInfos)

        }
    }

}