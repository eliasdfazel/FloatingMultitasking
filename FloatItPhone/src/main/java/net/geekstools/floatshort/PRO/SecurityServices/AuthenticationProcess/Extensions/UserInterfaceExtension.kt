/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Html
import android.view.WindowManager
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.PinPassword.AuthenticationPinPassword
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

object UserInterfaceExtraData {
    const val DoLockUnlock = "DoLockUnlock"

    const val PackageName = "PackageName"
    const val ClassName = "ClassName"

    const val OtherTitle = "OtherTitle"

    const val PrimaryColor = "PrimaryColor"
}

fun AuthenticationFingerprint.setupAuthenticationUIWindow() {
    val primaryColor: Int = intent.getIntExtra("PrimaryColor", getColor(R.color.default_color))

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT

    window.decorView.setBackgroundColor(functionsClassLegacy.setColorAlpha(primaryColor, 113f))
}

fun AuthenticationFingerprint.setupAuthenticationUIText() : String {

    var dialogueTitle = getString(R.string.securityServices) + "🔒"

    if (intent.hasExtra(UserInterfaceExtraData.PackageName)
            && intent.hasExtra(UserInterfaceExtraData.ClassName)) {

        val packageName = intent.getStringExtra(UserInterfaceExtraData.PackageName)!!
        val className = intent.getStringExtra(UserInterfaceExtraData.ClassName)!!

        val activityInformation: ActivityInfo? = packageManager.getActivityInfo(ComponentName(packageName, className),0)
        dialogueTitle = functionsClassLegacy.activityLabel(activityInformation)

    } else if (intent.hasExtra(UserInterfaceExtraData.OtherTitle)) {

        dialogueTitle = intent.getStringExtra(UserInterfaceExtraData.OtherTitle)!!
    }

    if (intent.getBooleanExtra(UserInterfaceExtraData.DoLockUnlock, false)) {
        dialogueTitle = "$dialogueTitle 🔓"
    } else {
        dialogueTitle = "$dialogueTitle 🔒"
    }

    return dialogueTitle
}

fun AuthenticationPinPassword.setupAuthenticationPinPasswordUI() {

    authDialogContentBinding.root.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

    authDialogContentBinding.cancelAuth.setTextColor(PublicVariable.colorLightDarkOpposite)
    authDialogContentBinding.cancelAuth.setBackgroundColor(PublicVariable.colorLightDark)
    authDialogContentBinding.cancelAuth.rippleColor = ColorStateList.valueOf(AuthenticationPinPassword.ExtraInformation.primaryColor)

    authDialogContentBinding.pinPasswordEditText.setHintTextColor(AuthenticationPinPassword.ExtraInformation.primaryColor)
    authDialogContentBinding.pinPasswordEditText.setTextColor(PublicVariable.colorLightDarkOpposite)

    authDialogContentBinding.textInputPinPassword.hintTextColor = ColorStateList.valueOf(AuthenticationPinPassword.ExtraInformation.primaryColor)
    authDialogContentBinding.textInputPinPassword.defaultHintTextColor = ColorStateList.valueOf(AuthenticationPinPassword.ExtraInformation.primaryColor)

    authDialogContentBinding.dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
    authDialogContentBinding.dialogueTitle.text = Html.fromHtml("<big><font color='" + PublicVariable.colorLightDarkOpposite + "'>"
            +
            AuthenticationPinPassword.ExtraInformation.dialogueTitle + " 🔒 "
            +
            "</font></big>")
}