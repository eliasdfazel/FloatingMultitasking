/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/19/20 8:26 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.RemoteTask.Remove

import android.app.Service
import android.content.Intent
import android.os.IBinder
import net.geekstools.floatshort.PRO.Folders.FloatingServices.FloatingFolders
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForApplications
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForFrequentlyApplications
import net.geekstools.floatshort.PRO.Shortcuts.PopupDialogue.PopupOptionsFloatingShortcuts
import net.geekstools.floatshort.PRO.Widgets.FloatingServices.WidgetUnlimitedFloating

class RemoveAll : Service() {

    override fun onBind(intent: Intent): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        /*Applications*/
        Intent(applicationContext, FloatingShortcutsForApplications::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        /*Folders*/
        Intent(applicationContext, FloatingFolders::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        /*Widgets*/
        Intent(applicationContext, WidgetUnlimitedFloating::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        /*Frequently*/
        Intent(applicationContext, FloatingShortcutsForFrequentlyApplications::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        /*PopupOptionsFloatingShortcuts*/
        Intent(applicationContext, PopupOptionsFloatingShortcuts::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            stopService(this@apply)
        }

        this@RemoveAll.stopSelf()

        return START_NOT_STICKY
    }
}