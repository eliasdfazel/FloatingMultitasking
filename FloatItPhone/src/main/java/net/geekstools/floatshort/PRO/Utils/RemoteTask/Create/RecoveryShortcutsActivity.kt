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
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class RecoveryShortcutsActivity : Activity() {
    override fun onCreate(Saved: Bundle?) {
        super.onCreate(Saved)

        if (intent.action == Intent.ACTION_CREATE_SHORTCUT) {
            val functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(applicationContext)

            val shapeTempDrawable: Drawable? = functionsClassLegacy.shapesDrawables()
            shapeTempDrawable?.setTint(PublicVariable.primaryColor)

            val recoveryDrawable = getDrawable(R.drawable.draw_widget_shortcuts) as LayerDrawable?
            recoveryDrawable?.setDrawableByLayerId(R.id.backgroundTemporary, shapeTempDrawable)

            val shortcutApp = Bitmap.createBitmap(recoveryDrawable?.intrinsicWidth?:50, recoveryDrawable?.intrinsicHeight?:50, Bitmap.Config.ARGB_8888)
            recoveryDrawable?.setBounds(0, 0, recoveryDrawable.intrinsicWidth, recoveryDrawable.intrinsicHeight)
            recoveryDrawable?.draw(Canvas(shortcutApp))

            val setCategoryRecovery = Intent(applicationContext, RecoveryShortcutsActivity::class.java)
            setCategoryRecovery.action = "Remote_Recover_Shortcuts"
            setCategoryRecovery.addCategory(Intent.CATEGORY_DEFAULT)

            Intent().apply {
                putExtra(Intent.EXTRA_SHORTCUT_INTENT, setCategoryRecovery)
                putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.recoveryShortcuts))
                putExtra(Intent.EXTRA_SHORTCUT_ICON, shortcutApp)
                setResult(RESULT_OK, this@apply)
            }

        } else if (intent.action == Intent.ACTION_MAIN || intent.action == Intent.ACTION_VIEW || intent.action == "Remote_Recover_Shortcuts") {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                startForegroundService(Intent(applicationContext, RecoveryShortcuts::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

            } else {

                startService(Intent(applicationContext, RecoveryShortcuts::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

            }
        }

        this@RecoveryShortcutsActivity.finish()
    }
}