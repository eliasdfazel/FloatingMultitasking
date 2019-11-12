/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:18 PM
 * Last modified 11/11/19 7:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widget.RoomDatabase

import androidx.room.*

@Dao
interface WidgetDataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewWidgetData(vararg arrayOfWidgetDataModels: WidgetDataModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWidgetData(vararg arrayOfWidgetDataModels: WidgetDataModel)

    @Delete
    fun delete(widgetDataModel: WidgetDataModel)

    @Query("SELECT * FROM WidgetData ORDER BY AppName ASC")
    fun getAllWidgetData(): List<WidgetDataModel>

    @Query("SELECT * FROM WidgetData WHERE PackageName IN (:PackageName) AND ClassNameProvider IN (:ClassNameWidgetProvider)")
    fun loadWidgetByClassNameProviderWidget(PackageName: String, ClassNameWidgetProvider: String): WidgetDataModel

    @Query("UPDATE WidgetData SET WidgetId = :WidgetId WHERE PackageName = :PackageName AND ClassNameProvider == :ClassNameProvider")
    fun updateWidgetIdByPackageNameClassName(PackageName: String, ClassNameProvider: String, WidgetId: Int): Int

    @Query("UPDATE WidgetData SET WidgetLabel = :WidgetLabel WHERE WidgetId = :WidgetId")
    fun updateWidgetLabelByWidgetId(WidgetId: Int, WidgetLabel: String): Int

    @Query("UPDATE WidgetData SET Recovery = :AddedWidgetRecovery WHERE PackageName= :PackageName AND ClassNameProvider = :ClassNameWidgetProvider")
    fun updateRecoveryByClassNameProviderWidget(PackageName: String, ClassNameWidgetProvider: String, AddedWidgetRecovery: Boolean): Int

    @Query("DELETE FROM WidgetData WHERE PackageName = :PackageName AND ClassNameProvider = :ClassNameWidgetProvider")
    fun deleteByWidgetClassNameProviderWidget(PackageName: String, ClassNameWidgetProvider: String)
}