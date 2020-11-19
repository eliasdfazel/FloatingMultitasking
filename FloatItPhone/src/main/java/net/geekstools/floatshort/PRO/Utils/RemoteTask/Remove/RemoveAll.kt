/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/19/20 7:52 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Utils.RemoteTask.Remove

import android.app.Service
import android.content.Intent
import android.os.IBinder
import net.geekstools.floatshort.PRO.Folders.FloatingServices.*
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.*
import net.geekstools.floatshort.PRO.Shortcuts.PopupDialogue.PopupOptionsFloatingShortcuts
import net.geekstools.floatshort.PRO.Widgets.FloatingServices.WidgetUnlimitedFloating

class RemoveAll : Service() {

    override fun onBind(intent: Intent): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        /*Applications*/
        Intent(applicationContext, FloatingShortcutsForApplications::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        Intent(applicationContext, FloatingShortcutsForWifi::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        Intent(applicationContext, FloatingShortcutsForBluetooth::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        Intent(applicationContext, FloatingShortcutsForGps::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        Intent(applicationContext, FloatingShortcutsForNfc::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        Intent(applicationContext, FloatingShortcutsForTime::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        /*Folders*/
        Intent(applicationContext, FloatingFolders::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        Intent(applicationContext, FloatingFoldersForWifi::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        Intent(applicationContext, FloatingFoldersForBluetooth::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        Intent(applicationContext, FloatingFoldersForGps::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        Intent(applicationContext, FloatingFoldersForNfc::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        Intent(applicationContext, FloatingFoldersForTime::class.java).apply {
            putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings))

            startService(this@apply)
        }

        /*HIS*/
        Intent(applicationContext, FloatingShortcutsForHIS::class.java).apply {
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

            startService(this@apply)
        }

        this@RemoveAll.stopSelf()

        return START_NOT_STICKY
    }
}