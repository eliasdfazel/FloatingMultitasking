/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.dialogue_message.*
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug.Companion.PrintDebug
import java.io.File

class FunctionsClassDialogues (var functionsClassDataActivity: FunctionsClassDataActivity, var functionsClass: FunctionsClass) {

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

        dialog.rateIt.setOnClickListener {
            dialog.dismiss()

            functionsClassDataActivity.activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(functionsClassDataActivity.activity.getString(R.string.play_store_link))))
        }

        dialog.followIt.setOnClickListener {
            dialog.dismiss()

            functionsClassDataActivity.activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(functionsClassDataActivity.activity.getString(R.string.link_facebook_app))))
        }

        dialog.setOnDismissListener {
            functionsClass.saveFile(".Updated", functionsClass.appVersionCode(functionsClassDataActivity.activity.packageName).toString())
        }

        if (!functionsClassDataActivity.activity.getFileStreamPath(".Updated").exists()) {
            dialog.show()
        } else if (functionsClass.appVersionCode(functionsClassDataActivity.activity.packageName) > functionsClass.readFile(".Updated").toInt()) {
            dialog.show()
            if (!BuildConfig.DEBUG && functionsClass.networkConnection()) {
                FirebaseAuth.getInstance().addAuthStateListener {
                    val user: FirebaseUser? = it.currentUser
                    if (user == null) {
                        functionsClass.savePreference(".UserInformation", "userEmail", null)
                    } else {
                        val betaFile = File("/data/data/" + functionsClassDataActivity.activity.packageName + "/shared_prefs/.UserInformation.xml")
                        val uriBetaFile = Uri.fromFile(betaFile)
                        val firebaseStorage = FirebaseStorage.getInstance()

                        val storageReference = firebaseStorage.getReference("/Users/" + "API" + functionsClass.returnAPI() + "/" +
                                functionsClass.readPreference(".UserInformation", "userEmail", null))
                        val uploadTask = storageReference.putFile(uriBetaFile)

                        uploadTask.addOnFailureListener { exception -> exception.printStackTrace() }.addOnSuccessListener { PrintDebug("Firebase Activities Done Successfully") }
                    }
                }
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
            functionsClass.saveFile(".Updated", functionsClass.appVersionCode(functionsClassDataActivity.activity.packageName).toString())
        }

        dialog.show()
    }
}