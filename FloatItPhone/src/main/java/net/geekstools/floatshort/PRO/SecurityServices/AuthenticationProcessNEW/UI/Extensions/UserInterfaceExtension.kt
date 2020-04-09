package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.UI.Extensions

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Html
import android.view.WindowManager
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.UI.AuthenticationFingerprintUI
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.UI.AuthenticationPinPasswordUI
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

fun AuthenticationFingerprintUI.setupAuthenticationUIWindow() {
    val primaryColor: Int = intent.getIntExtra("PrimaryColor", getColor(R.color.default_color))

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT

    window.decorView.setBackgroundColor(functionsClass.setColorAlpha(primaryColor, 113f))
}

fun AuthenticationFingerprintUI.setupAuthenticationUIText() : String {

    var dialogueTitle = getString(R.string.app_name)

    if (intent.hasExtra("PackageName")
            && intent.hasExtra("ClassName")) {

        val packageName = intent.getStringExtra("PackageName")!!
        val className = intent.getStringExtra("ClassName")!!

        val activityInformation: ActivityInfo? = packageManager.getActivityInfo(ComponentName(packageName, className),0)
        dialogueTitle = functionsClass.activityLabel(activityInformation)

    } else if (intent.hasExtra("OtherTitle")) {

        dialogueTitle = intent.getStringExtra("OtherTitle")!!
    }

    return dialogueTitle
}

fun AuthenticationPinPasswordUI.setupAuthenticationPinPasswordUI() {
    authDialogContentBinding.root.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

    authDialogContentBinding.cancelAuth.setTextColor(PublicVariable.colorLightDarkOpposite)
    authDialogContentBinding.cancelAuth.setBackgroundColor(PublicVariable.colorLightDark)
    authDialogContentBinding.cancelAuth.rippleColor = ColorStateList.valueOf(AuthenticationPinPasswordUI.ExtraInformation.primaryColor)

    authDialogContentBinding.pinPasswordEditText.setHintTextColor(AuthenticationPinPasswordUI.ExtraInformation.primaryColor)
    authDialogContentBinding.pinPasswordEditText.setTextColor(PublicVariable.colorLightDarkOpposite)

    authDialogContentBinding.textInputPinPassword.hintTextColor = ColorStateList.valueOf(AuthenticationPinPasswordUI.ExtraInformation.primaryColor)
    authDialogContentBinding.textInputPinPassword.defaultHintTextColor = ColorStateList.valueOf(AuthenticationPinPasswordUI.ExtraInformation.primaryColor)

    authDialogContentBinding.dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
    authDialogContentBinding.dialogueTitle.text = Html.fromHtml("<big><font color='" + PublicVariable.colorLightDarkOpposite + "'>"
            +
            AuthenticationPinPasswordUI.ExtraInformation.dialogueTitle + " 🔒 "
            +
            "</font></big>")
}