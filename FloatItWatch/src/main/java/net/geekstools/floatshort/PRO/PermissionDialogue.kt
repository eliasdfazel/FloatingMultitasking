/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/26/20 7:21 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.wearable.activity.WearableActivity
import android.text.Html
import android.widget.Toast
import java.util.*

class PermissionDialogue : WearableActivity() {

    private companion object {
        const val RuntimePermissionRequestCode = 666
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setAmbientEnabled()

        permissionsDialogue()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PermissionDialogue.RuntimePermissionRequestCode) {

            if (Settings.canDrawOverlays(applicationContext)) {

                startActivity(Intent(applicationContext, Configurations::class.java),
                        ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

                this@PermissionDialogue.finish()
            } else {

                this@PermissionDialogue.finish()
            }
        }
    }

    private fun permissionsDialogue() {
        val alertDialog = AlertDialog.Builder(this@PermissionDialogue)
        alertDialog.setTitle(Html.fromHtml("<font color='" + getColor(R.color.default_color) + "'>" +
                resources.getString(R.string.permTitle) + "</font>"))
        alertDialog.setMessage(
                Html.fromHtml("<font color='" + getColor(R.color.dark) + "'>" +
                        resources.getString(R.string.permDesc) + "</font>"))
        alertDialog.setIcon(getDrawable(R.drawable.ic_launcher))
        alertDialog.setCancelable(false)

        alertDialog.setPositiveButton(getString(R.string.grant)) { dialog, which ->

            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, PermissionDialogue.RuntimePermissionRequestCode)

            Toast.makeText(applicationContext,
                    resources.getString(R.string.overlayset).toUpperCase(Locale.getDefault()), Toast.LENGTH_LONG).show()

            dialog.dismiss()
        }

        alertDialog.setNegativeButton(getString(R.string.later)) { dialog, which ->
            this@PermissionDialogue.finish()

            dialog.dismiss()
        }

        alertDialog.setNeutralButton(getString(R.string.read)) { dialog, which ->
            this@PermissionDialogue.finish()

            dialog.dismiss()
        }

        alertDialog.setOnDismissListener {

        }
        alertDialog.show()
    }
}