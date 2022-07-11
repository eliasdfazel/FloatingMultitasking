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
import kotlinx.android.synthetic.main.dialogue_message.*
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.InAppReview.InAppReviewProcess

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

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialogue_message)
        dialog.window?.attributes = layoutParams
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(true)

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

        dialog.dialogueTitle.text = activity.getString(R.string.whatsnew)
        dialog.dialogueMessage.text = Html.fromHtml(activity.getString(R.string.changelog), Html.FROM_HTML_MODE_COMPACT)

        dialog.rateIt.setBackgroundColor(if (PublicVariable.themeLightDark) { activity.getColor(R.color.lighter) } else { activity.getColor(R.color.darker) })
        dialog.followIt.setBackgroundColor(if (PublicVariable.themeLightDark) { activity.getColor(R.color.lighter) } else { activity.getColor(R.color.darker) })

        dialog.dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.dialogueMessage.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.followIt.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.setOnClickListener {
            dialog.dismiss()

            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.play_store_link))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        dialog.followIt.setOnClickListener {
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

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialogue_message)
        dialog.window!!.attributes = layoutParams
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(true)

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

        dialog.dialogueTitle.text = activity.getString(R.string.whatsnew)
        dialog.dialogueMessage.text = Html.fromHtml(activity.getString(R.string.changelog))

        dialog.rateIt.setBackgroundColor(if (PublicVariable.themeLightDark) { activity.getColor(R.color.lighter) } else { activity.getColor(R.color.darker) })
        dialog.followIt.setBackgroundColor(if (PublicVariable.themeLightDark) { activity.getColor(R.color.lighter) } else { activity.getColor(R.color.darker) })

        dialog.dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.dialogueMessage.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.followIt.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.text = if (betaChangeLog.contains(activity.packageName)) {
            activity.getString(R.string.shareIt)
        } else {
            activity.getString(R.string.betaUpdate)
        }
        dialog.rateIt.setOnClickListener {
            dialog.dismiss()

            if (dialog.rateIt.text == activity.getString(R.string.shareIt)) {
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.play_store_link).toString() + activity.getPackageName()))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } else if (dialog.rateIt.text == activity.getString(R.string.betaUpdate)) {
                functionsClassLegacy.upcomingChangeLog(activity, betaChangeLog, betaVersionCode)
            }
        }

        dialog.followIt.setOnClickListener {
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