/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/25/20 4:29 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

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

class Dialogues (var functionsClassDataActivity: FunctionsClassDataActivity, var functionsClass: FunctionsClass) {

    private val fileIO: FileIO = FileIO(functionsClassDataActivity.activity)

    fun changeLog() {
        val layoutParams = WindowManager.LayoutParams()
        val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 370f, functionsClassDataActivity.activity.resources.displayMetrics).toInt()
        val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 430f, functionsClassDataActivity.activity.resources.displayMetrics).toInt()

        layoutParams.width = dialogueWidth
        layoutParams.height = dialogueHeight
        layoutParams.windowAnimations = android.R.style.Animation_Dialog
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.57f

        val dialog = Dialog(functionsClassDataActivity.activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialogue_message)
        dialog.window?.attributes = layoutParams
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(true)

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

        dialog.dialogueTitle.text = functionsClassDataActivity.activity.getString(R.string.whatsnew)
        dialog.dialogueMessage.text = Html.fromHtml(functionsClassDataActivity.activity.getString(R.string.changelog))

        dialog.rateIt.setBackgroundColor(if (PublicVariable.themeLightDark) { functionsClassDataActivity.activity.getColor(R.color.lighter) } else { functionsClassDataActivity.activity.getColor(R.color.darker) })
        dialog.followIt.setBackgroundColor(if (PublicVariable.themeLightDark) { functionsClassDataActivity.activity.getColor(R.color.lighter) } else { functionsClassDataActivity.activity.getColor(R.color.darker) })

        dialog.dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.dialogueMessage.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.followIt.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.setOnClickListener {
            dialog.dismiss()

            functionsClassDataActivity.activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(functionsClassDataActivity.activity.getString(R.string.play_store_link))))
        }

        dialog.followIt.setOnClickListener {
            dialog.dismiss()

            functionsClassDataActivity.activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(functionsClassDataActivity.activity.getString(R.string.link_facebook_app))))
        }

        dialog.setOnDismissListener {

            if (functionsClass.applicationVersionCode(functionsClassDataActivity.activity.packageName) > fileIO.readFile(".Updated")?.toInt()?:0) {

                if (!functionsClassDataActivity.activity.isFinishing) {

                    InAppReviewProcess(functionsClassDataActivity.activity as AppCompatActivity).start()

                }

            }

            fileIO.saveFile(".Updated", functionsClass.applicationVersionCode(functionsClassDataActivity.activity.packageName).toString())
        }

        if (!functionsClassDataActivity.activity.getFileStreamPath(".Updated").exists()) {

            if (!functionsClassDataActivity.activity.isFinishing) {
                dialog.show()
            }

        } else if (functionsClass.applicationVersionCode(functionsClassDataActivity.activity.packageName) > fileIO.readFile(".Updated")?.toInt()?:0) {

            if (!functionsClassDataActivity.activity.isFinishing) {
                dialog.show()
            }

        }
    }

    fun changeLogPreference(betaChangeLog: String, betaVersionCode: String) {
        val layoutParams = WindowManager.LayoutParams()
        val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 370f, functionsClassDataActivity.activity.resources.displayMetrics).toInt()
        val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 430f, functionsClassDataActivity.activity.resources.displayMetrics).toInt()

        layoutParams.width = dialogueWidth
        layoutParams.height = dialogueHeight
        layoutParams.windowAnimations = android.R.style.Animation_Dialog
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.57f

        val dialog = Dialog(functionsClassDataActivity.activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialogue_message)
        dialog.window!!.attributes = layoutParams
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(true)

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

        dialog.dialogueTitle.text = functionsClassDataActivity.activity.getString(R.string.whatsnew)
        dialog.dialogueMessage.text = Html.fromHtml(functionsClassDataActivity.activity.getString(R.string.changelog))

        dialog.rateIt.setBackgroundColor(if (PublicVariable.themeLightDark) { functionsClassDataActivity.activity.getColor(R.color.lighter) } else { functionsClassDataActivity.activity.getColor(R.color.darker) })
        dialog.followIt.setBackgroundColor(if (PublicVariable.themeLightDark) { functionsClassDataActivity.activity.getColor(R.color.lighter) } else { functionsClassDataActivity.activity.getColor(R.color.darker) })

        dialog.dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.dialogueMessage.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.followIt.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.text = if (betaChangeLog.contains(functionsClassDataActivity.activity.packageName)) {
            functionsClassDataActivity.activity.getString(R.string.shareIt)
        } else {
            functionsClassDataActivity.activity.getString(R.string.betaUpdate)
        }
        dialog.rateIt.setOnClickListener {
            dialog.dismiss()

            if (dialog.rateIt.text == functionsClassDataActivity.activity.getString(R.string.shareIt)) {
                functionsClassDataActivity.activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(functionsClassDataActivity.activity.getString(R.string.play_store_link).toString() + functionsClassDataActivity.activity.getPackageName()))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } else if (dialog.rateIt.text == functionsClassDataActivity.activity.getString(R.string.betaUpdate)) {
                functionsClass.upcomingChangeLog(functionsClassDataActivity.activity, betaChangeLog, betaVersionCode)
            }
        }

        dialog.followIt.setOnClickListener {
            dialog.dismiss()

            functionsClassDataActivity.activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(functionsClassDataActivity.activity.getString(R.string.link_facebook_app))))
        }

        dialog.setOnDismissListener {
            fileIO.saveFile(".Updated", functionsClass.applicationVersionCode(functionsClassDataActivity.activity.packageName).toString())
        }

        if (!functionsClassDataActivity.activity.isFinishing) {
            dialog.show()
        }
    }
}