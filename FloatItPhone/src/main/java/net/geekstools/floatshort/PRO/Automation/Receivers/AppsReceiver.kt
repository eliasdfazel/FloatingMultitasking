/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/20/20 5:03 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Automation.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassIO

class AppsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val functionsClass = FunctionsClass(context)
            val functionsClassIO = FunctionsClassIO(context)

            if (intent.action == Intent.ACTION_PACKAGE_FULLY_REMOVED) {

                val packageName = intent.data!!.encodedSchemeSpecificPart
                functionsClassIO.removeLine(".uFile", packageName)

                functionsClass.addAppShortcuts()

            }

        }

    }
}