/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/8/20 4:26 AM
 * Last modified 1/8/20 4:19 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.Functions

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
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug.Companion.PrintDebug
import java.io.File

class FunctionsClassUI (functionsClassDataActivity: FunctionsClassDataActivity, functionsClass: FunctionsClass) {

    private val initFunctionsClassDataActivity: FunctionsClassDataActivity = functionsClassDataActivity
    private val initFunctionsClass: FunctionsClass = functionsClass

    fun ChangeLog() {
        val layoutParams = WindowManager.LayoutParams()
        val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 370f, initFunctionsClassDataActivity.activity.resources.displayMetrics).toInt()
        val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 430f, initFunctionsClassDataActivity.activity.resources.displayMetrics).toInt()

        layoutParams.width = dialogueWidth
        layoutParams.height = dialogueHeight
        layoutParams.windowAnimations = android.R.style.Animation_Dialog
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.57f

        val dialog = Dialog(initFunctionsClassDataActivity.activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialogue_message)
        dialog.window!!.attributes = layoutParams
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(true)

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

        dialog.dialogueTitle.text = initFunctionsClassDataActivity.activity.getString(R.string.whatsnew)
        dialog.dialogueMessage.text = Html.fromHtml(initFunctionsClassDataActivity.activity.getString(R.string.changelog))

        dialog.rateIt.setBackgroundColor(if (PublicVariable.themeLightDark) { initFunctionsClassDataActivity.activity.getColor(R.color.lighter) } else { initFunctionsClassDataActivity.activity.getColor(R.color.darker) })
        dialog.followIt.setBackgroundColor(if (PublicVariable.themeLightDark) { initFunctionsClassDataActivity.activity.getColor(R.color.lighter) } else { initFunctionsClassDataActivity.activity.getColor(R.color.darker) })

        dialog.dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.dialogueMessage.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.followIt.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.setOnClickListener {
            dialog.dismiss()

            initFunctionsClassDataActivity.activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(initFunctionsClassDataActivity.activity.getString(R.string.play_store_link))))
        }

        dialog.followIt.setOnClickListener {
            dialog.dismiss()

            initFunctionsClassDataActivity.activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(initFunctionsClassDataActivity.activity.getString(R.string.link_facebook_app))))
        }

        dialog.setOnDismissListener {
            initFunctionsClass.saveFile(".Updated", initFunctionsClass.appVersionCode(initFunctionsClassDataActivity.activity.packageName).toString())
        }

        if (!initFunctionsClassDataActivity.activity.getFileStreamPath(".Updated").exists()) {
            dialog.show()
        } else if (initFunctionsClass.appVersionCode(initFunctionsClassDataActivity.activity.packageName) > initFunctionsClass.readFile(".Updated").toInt()) {
            dialog.show()
            if (!BuildConfig.DEBUG && initFunctionsClass.networkConnection()) {
                FirebaseAuth.getInstance().addAuthStateListener {
                    val user: FirebaseUser? = it.currentUser
                    if (user == null) {
                        initFunctionsClass.savePreference(".UserInformation", "userEmail", null)
                    } else {
                        val betaFile = File("/data/data/" + initFunctionsClassDataActivity.activity.packageName + "/shared_prefs/.UserInformation.xml")
                        val uriBetaFile = Uri.fromFile(betaFile)
                        val firebaseStorage = FirebaseStorage.getInstance()

                        val storageReference = firebaseStorage.getReference("/Users/" + "API" + initFunctionsClass.returnAPI() + "/" +
                                initFunctionsClass.readPreference(".UserInformation", "userEmail", null))
                        val uploadTask = storageReference.putFile(uriBetaFile)

                        uploadTask.addOnFailureListener { exception -> exception.printStackTrace() }.addOnSuccessListener { PrintDebug("Firebase Activities Done Successfully") }
                    }
                }
            }
        }
    }

    fun ChangeLogPreference(betaChangeLog: String, betaVersionCode: String) {
        val layoutParams = WindowManager.LayoutParams()
        val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 370f, initFunctionsClassDataActivity.activity.resources.displayMetrics).toInt()
        val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 430f, initFunctionsClassDataActivity.activity.resources.displayMetrics).toInt()

        layoutParams.width = dialogueWidth
        layoutParams.height = dialogueHeight
        layoutParams.windowAnimations = android.R.style.Animation_Dialog
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.57f

        val dialog = Dialog(initFunctionsClassDataActivity.activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialogue_message)
        dialog.window!!.attributes = layoutParams
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(true)

        val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
        dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

        dialog.dialogueTitle.text = initFunctionsClassDataActivity.activity.getString(R.string.whatsnew)
        dialog.dialogueMessage.text = Html.fromHtml(initFunctionsClassDataActivity.activity.getString(R.string.changelog))

        dialog.rateIt.setBackgroundColor(if (PublicVariable.themeLightDark) { initFunctionsClassDataActivity.activity.getColor(R.color.lighter) } else { initFunctionsClassDataActivity.activity.getColor(R.color.darker) })
        dialog.followIt.setBackgroundColor(if (PublicVariable.themeLightDark) { initFunctionsClassDataActivity.activity.getColor(R.color.lighter) } else { initFunctionsClassDataActivity.activity.getColor(R.color.darker) })

        dialog.dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.dialogueMessage.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.setTextColor(PublicVariable.colorLightDarkOpposite)
        dialog.followIt.setTextColor(PublicVariable.colorLightDarkOpposite)

        dialog.rateIt.text = if (betaChangeLog.contains(initFunctionsClassDataActivity.activity.packageName)) {
            initFunctionsClassDataActivity.activity.getString(R.string.shareIt)
        } else {
            initFunctionsClassDataActivity.activity.getString(R.string.betaUpdate)
        }
        dialog.rateIt.setOnClickListener {
            dialog.dismiss()

            if (dialog.rateIt.text == initFunctionsClassDataActivity.activity.getString(R.string.shareIt)) {
                initFunctionsClassDataActivity.activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(initFunctionsClassDataActivity.activity.getString(R.string.play_store_link).toString() + initFunctionsClassDataActivity.activity.getPackageName()))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } else if (dialog.rateIt.text == initFunctionsClassDataActivity.activity.getString(R.string.betaUpdate)) {
                initFunctionsClass.upcomingChangeLog(initFunctionsClassDataActivity.activity, betaChangeLog, betaVersionCode)
            }
        }

        dialog.followIt.setOnClickListener {
            dialog.dismiss()

            initFunctionsClassDataActivity.activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(initFunctionsClassDataActivity.activity.getString(R.string.link_facebook_app))))
        }

        dialog.setOnDismissListener {
            initFunctionsClass.saveFile(".Updated", initFunctionsClass.appVersionCode(initFunctionsClassDataActivity.activity.packageName).toString())
        }

        dialog.show()
    }
}