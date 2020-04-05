package net.geekstools.floatshort.PRO.Folders.Utils

import android.view.animation.Animation
import net.geekstools.floatshort.PRO.Folders.UI.AppsConfirmButton

interface ConfirmButtonViewInterface {
    fun makeItVisible(appsConfirmButton: AppsConfirmButton)
    fun startCustomAnimation(appsConfirmButton: AppsConfirmButton, animation: Animation?)
    fun setDismissBackground(appsConfirmButton: AppsConfirmButton)
}