/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class RecoveryFoldersActivity : Activity() {

    override fun onCreate(Saved: Bundle?) {
        super.onCreate(Saved)

        if (intent.action == Intent.ACTION_CREATE_SHORTCUT) {

            val functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(applicationContext)

            val shapeTempDrawable: Drawable? = functionsClassLegacy.shapesDrawables()
            shapeTempDrawable?.setTint(PublicVariable.primaryColor)

            val folderDrawable = getDrawable(R.drawable.draw_widget_folders) as LayerDrawable?
            folderDrawable?.setDrawableByLayerId(R.id.backgroundTemporary, shapeTempDrawable)

            val shortcutApp = Bitmap.createBitmap(folderDrawable?.intrinsicWidth?:50, folderDrawable?.intrinsicHeight?:50, Bitmap.Config.ARGB_8888)
            folderDrawable?.setBounds(0, 0, folderDrawable.intrinsicWidth, folderDrawable.intrinsicHeight)
            folderDrawable?.draw(Canvas(shortcutApp))

            val setCategoryRecovery = Intent(applicationContext, RecoveryFoldersActivity::class.java)
            setCategoryRecovery.action = "Remote_Recover_Categories"
            setCategoryRecovery.addCategory(Intent.CATEGORY_DEFAULT)

            Intent().apply {
                putExtra(Intent.EXTRA_SHORTCUT_INTENT, setCategoryRecovery)
                putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.recover_folder))
                putExtra(Intent.EXTRA_SHORTCUT_ICON, shortcutApp)
                setResult(RESULT_OK, this@apply)
            }

        } else if (intent.action == Intent.ACTION_MAIN || intent.action == Intent.ACTION_VIEW || intent.action == "Remote_Recover_Categories") {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                startForegroundService(Intent(applicationContext, RecoveryFolders::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

            } else {

                startService(Intent(applicationContext, RecoveryFolders::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

            }

            if (PublicVariable.allFloatingCounter == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(Intent(applicationContext, BindServices::class.java))
                } else {
                    startService(Intent(applicationContext, BindServices::class.java))
                }
            }

        }

        this@RecoveryFoldersActivity.finish()
    }
}