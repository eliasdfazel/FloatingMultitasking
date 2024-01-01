/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.InAppReview.InAppReviewProcess
import net.geekstools.floatshort.PRO.databinding.DialogueMessageBinding

class Dialogues (var activity: Activity, var functionsClassLegacy: FunctionsClassLegacy) {

    private val fileIO: FileIO = FileIO(activity)

    fun changeLog() {
        val layoutParams = WindowManager.LayoutParams()
        val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 370f, activity.resources.displayMetrics).toInt()
        val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 430f, activity.resources.displayMetrics).toInt()

        layoutParams.width = dialogueWidth
        layoutParams.height = dialogueHeight
        layoutParams.windowAnimations = android.R.style.Animation_Dialog
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.57f

        val dialogueMessageBinding = DialogueMessageBinding.inflate(activity.layoutInflater)

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogueMessageBinding.root)
        dialog.window?.attributes = layoutParams
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(true)

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

        dialogueMessageBinding.dialogueTitle.text = activity.getString(R.string.whatsnew)
        dialogueMessageBinding.dialogueMessage.text = Html.fromHtml(activity.getString(R.string.changelog), Html.FROM_HTML_MODE_COMPACT)

        dialogueMessageBinding.rateIt.setBackgroundColor(if (PublicVariable.themeLightDark) { activity.getColor(R.color.lighter) } else { activity.getColor(R.color.darker) })
        dialogueMessageBinding.followIt.setBackgroundColor(if (PublicVariable.themeLightDark) { activity.getColor(R.color.lighter) } else { activity.getColor(R.color.darker) })

        dialogueMessageBinding.dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialogueMessageBinding.dialogueMessage.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialogueMessageBinding.rateIt.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialogueMessageBinding.followIt.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialogueMessageBinding.rateIt.setOnClickListener {

            val shareText = activity.getString(R.string.shareTitle) +
                    "\n" + activity.getString(R.string.shareSummary) +
                    "\n" + activity.getString(R.string.play_store_link) + activity.packageName
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.putExtra(Intent.EXTRA_TEXT, shareText)
                this.type = "text/plain"
            }
            activity.startActivity(sharingIntent)

        }

        dialogueMessageBinding.followIt.setOnClickListener {
            dialog.dismiss()

            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.link_facebook_app))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        dialog.setOnDismissListener {

            if (functionsClassLegacy.applicationVersionCode(activity.packageName) > fileIO.readFile(".Updated")?.toInt()?:0) {

                if (!activity.isFinishing) {

                    InAppReviewProcess(activity as AppCompatActivity).start()

                }

            }

            fileIO.saveFile(".Updated", functionsClassLegacy.applicationVersionCode(activity.packageName).toString())
        }

        if (!activity.getFileStreamPath(".Updated").exists()) {

            if (!activity.isFinishing) {
                dialog.show()
            }

        } else if (functionsClassLegacy.applicationVersionCode(activity.packageName) > fileIO.readFile(".Updated")?.toInt()?:0) {

            if (!activity.isFinishing) {
                dialog.show()
            }

        }
    }

    fun changeLogPreference(betaChangeLog: String, betaVersionCode: String) {
        val layoutParams = WindowManager.LayoutParams()
        val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 370f, activity.resources.displayMetrics).toInt()
        val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 430f, activity.resources.displayMetrics).toInt()

        layoutParams.width = dialogueWidth
        layoutParams.height = dialogueHeight
        layoutParams.windowAnimations = android.R.style.Animation_Dialog
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.57f

        val dialogueMessageBinding = DialogueMessageBinding.inflate(activity.layoutInflater)

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogueMessageBinding.root)
        dialog.window!!.attributes = layoutParams
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(true)

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

        dialogueMessageBinding.dialogueTitle.text = activity.getString(R.string.whatsnew)
        dialogueMessageBinding.dialogueMessage.text = Html.fromHtml(activity.getString(R.string.changelog), Html.FROM_HTML_MODE_COMPACT)

        dialogueMessageBinding.rateIt.setBackgroundColor(if (PublicVariable.themeLightDark) { activity.getColor(R.color.lighter) } else { activity.getColor(R.color.darker) })
        dialogueMessageBinding.followIt.setBackgroundColor(if (PublicVariable.themeLightDark) { activity.getColor(R.color.lighter) } else { activity.getColor(R.color.darker) })

        dialogueMessageBinding.dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialogueMessageBinding.dialogueMessage.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialogueMessageBinding.rateIt.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialogueMessageBinding.followIt.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialogueMessageBinding.rateIt.setOnClickListener {

            val shareText = activity.getString(R.string.shareTitle) +
                    "\n" + activity.getString(R.string.shareSummary) +
                    "\n" + activity.getString(R.string.play_store_link) + activity.packageName
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.putExtra(Intent.EXTRA_TEXT, shareText)
                this.type = "text/plain"
            }
            activity.startActivity(sharingIntent)

        }

        dialogueMessageBinding.followIt.setOnClickListener {
            dialog.dismiss()

            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.link_facebook_app))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        dialog.setOnDismissListener {
            fileIO.saveFile(".Updated", functionsClassLegacy.applicationVersionCode(activity.packageName).toString())
        }

        if (!activity.isFinishing) {
            dialog.show()
        }
    }
}