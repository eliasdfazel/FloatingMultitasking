package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.UI.Extensions

import android.graphics.Color
import android.view.WindowManager
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.UI.AuthenticationUI

fun AuthenticationUI.setupAthenticationUIWindow() {
    val primaryColor: Int = intent.getIntExtra("PrimaryColor", getColor(R.color.default_color))

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT

    window.decorView.setBackgroundColor(functionsClass.setColorAlpha(primaryColor, 113f))
}