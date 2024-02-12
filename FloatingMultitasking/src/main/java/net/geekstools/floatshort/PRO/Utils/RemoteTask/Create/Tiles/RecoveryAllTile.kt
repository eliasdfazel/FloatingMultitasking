/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/18/20 6:18 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.Tiles

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryAll

@RequiresApi(Build.VERSION_CODES.N)
class RecoveryAllTile : TileService() {

    override fun onClick() {
        super.onClick()

        qsTile.state = Tile.STATE_ACTIVE

        startService(Intent(applicationContext, RecoveryAll::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })

    }

}