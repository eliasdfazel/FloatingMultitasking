/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/28/20 3:59 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Automation.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class AppsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val functionsClass = FunctionsClass(context)

            if (intent.action == Intent.ACTION_PACKAGE_FULLY_REMOVED) {

                val packageName = intent.data!!.encodedSchemeSpecificPart
                functionsClass.removeLine(".uFile", packageName)

                functionsClass.addAppShortcuts()

            }

        }

    }
}