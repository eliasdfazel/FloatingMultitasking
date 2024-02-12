/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:18 PM
 * Last modified 11/11/19 7:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widgets.RoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WidgetDataModel::class], version = 100000, exportSchema = false)
abstract class WidgetDataInterface : RoomDatabase() {
    abstract fun initDataAccessObject(): WidgetDataDAO
}