/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/25/20 5:20 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Automation.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.PopupApplicationShortcuts

class AppsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val popupApplicationShortcuts: PopupApplicationShortcuts = PopupApplicationShortcuts(context)
            val fileIO = FileIO(context)

            if (intent.action == Intent.ACTION_PACKAGE_FULLY_REMOVED) {

                val packageName = intent.data!!.encodedSchemeSpecificPart
                fileIO.removeLine(".uFile", packageName)

                popupApplicationShortcuts.addPopupApplicationShortcuts()

            }

        }

    }
}