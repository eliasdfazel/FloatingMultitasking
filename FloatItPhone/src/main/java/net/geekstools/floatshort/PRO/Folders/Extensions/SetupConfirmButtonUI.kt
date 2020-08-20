/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/20/20 5:46 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.Extensions

import android.widget.RelativeLayout
import net.geekstools.floatshort.PRO.Folders.FoldersApplicationsSelectionProcess.AppSelectionList
import net.geekstools.floatshort.PRO.Folders.UI.AppsConfirmButton
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface

fun AppSelectionList.setupConfirmButtonUI(confirmButtonProcessInterface: ConfirmButtonProcessInterface) : AppsConfirmButton {

    val confirmButtonLayoutParams = RelativeLayout.LayoutParams(functionsClass.DpToInteger(63), functionsClass.DpToInteger(63))

    val appsConfirmButton = AppsConfirmButton(this@setupConfirmButtonUI, applicationContext,
            functionsClass,
            functionsClassIO,
            confirmButtonProcessInterface)

    appsConfirmButton.layoutParams = confirmButtonLayoutParams
    appsConfirmButton.bringToFront()

    advanceAppSelectionListBinding.confirmLayout.addView(appsConfirmButton)
    appsConfirmButton.setOnClickListener {

    }

    return appsConfirmButton
}