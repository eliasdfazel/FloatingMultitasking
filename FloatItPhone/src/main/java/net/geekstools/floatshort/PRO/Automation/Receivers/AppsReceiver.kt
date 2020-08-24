/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 6:17 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Automation.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class AppsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val functionsClass = FunctionsClass(context)
            val functionsClassIO = FileIO(context)

            if (intent.action == Intent.ACTION_PACKAGE_FULLY_REMOVED) {

                val packageName = intent.data!!.encodedSchemeSpecificPart
                functionsClassIO.removeLine(".uFile", packageName)

                functionsClass.addAppShortcuts()

            }

        }

    }
}