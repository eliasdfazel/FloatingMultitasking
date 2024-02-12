package net.geekstools.floatshort.PRO.Folders.Utils

import android.view.animation.Animation

interface ConfirmButtonViewInterface {
    fun makeItVisible()
    fun startCustomAnimation(animation: Animation?)
    fun setDismissBackground()
}