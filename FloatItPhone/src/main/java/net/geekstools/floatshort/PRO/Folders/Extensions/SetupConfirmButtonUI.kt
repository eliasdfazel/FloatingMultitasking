package net.geekstools.floatshort.PRO.Folders.Extensions

import android.widget.RelativeLayout
import net.geekstools.floatshort.PRO.Folders.AppSelectionList
import net.geekstools.floatshort.PRO.Folders.UI.AppsConfirmButton
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface

fun AppSelectionList.setupConfirmButtonUI(confirmButtonProcessInterface: ConfirmButtonProcessInterface) {

    val confirmButtonLayoutParams = RelativeLayout.LayoutParams(functionsClass.DpToInteger(63), functionsClass.DpToInteger(63))

    val appsConfirmButton = AppsConfirmButton(this@setupConfirmButtonUI, applicationContext,
            functionsClass,
            confirmButtonProcessInterface)
    appsConfirmButton.layoutParams = confirmButtonLayoutParams
    advanceAppSelectionListBinding.confirmLayout.addView(appsConfirmButton)

    println(">>>>>>>>>>>>>>>>> 1")
    appsConfirmButton.bringToFront()
    appsConfirmButton.setOnClickListener {

    }
}