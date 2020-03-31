/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/28/20 4:56 PM
 * Last modified 3/28/20 4:54 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widgets.RoomDatabase

import androidx.room.*

@Dao
interface WidgetDataDAO {

    @Query("SELECT * FROM WidgetData WHERE PackageName IN (:PackageName) AND ClassNameProvider IN (:ClassNameWidgetProvider)")
    fun loadWidgetByClassNameProviderWidget(PackageName: String, ClassNameWidgetProvider: String): WidgetDataModel


    @Query("UPDATE WidgetData SET Recovery = :AddedWidgetRecovery WHERE PackageName= :PackageName AND ClassNameProvider = :ClassNameWidgetProvider")
    fun updateRecoveryByClassNameProviderWidget(PackageName: String, ClassNameWidgetProvider: String, AddedWidgetRecovery: Boolean): Int


    @Query("DELETE FROM WidgetData WHERE PackageName = :PackageName AND ClassNameProvider = :ClassNameWidgetProvider")
    fun deleteByWidgetClassNameProviderWidget(PackageName: String, ClassNameWidgetProvider: String)

    /*
    *
    *
    * */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewWidgetDataSuspend(vararg arrayOfWidgetDataModels: WidgetDataModel)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWidgetDataSuspend(vararg arrayOfWidgetDataModels: WidgetDataModel)


    @Delete
    suspend fun deleteSuspend(widgetDataModel: WidgetDataModel)


    @Query("SELECT * FROM WidgetData ORDER BY AppName ASC")
    suspend fun getAllWidgetDataSuspend(): List<WidgetDataModel>


    @Query("SELECT * FROM WidgetData WHERE PackageName IN (:PackageName) AND ClassNameProvider IN (:ClassNameWidgetProvider)")
    suspend fun loadWidgetByClassNameProviderWidgetSuspend(PackageName: String, ClassNameWidgetProvider: String): WidgetDataModel


    @Query("UPDATE WidgetData SET WidgetId = :WidgetId WHERE PackageName = :PackageName AND ClassNameProvider == :ClassNameProvider")
    suspend fun updateWidgetIdByPackageNameClassNameSuspend(PackageName: String, ClassNameProvider: String, WidgetId: Int): Int


    @Query("UPDATE WidgetData SET WidgetLabel = :WidgetLabel WHERE WidgetId = :WidgetId")
    suspend fun updateWidgetLabelByWidgetIdSuspend(WidgetId: Int, WidgetLabel: String): Int


    @Query("UPDATE WidgetData SET Recovery = :AddedWidgetRecovery WHERE PackageName= :PackageName AND ClassNameProvider = :ClassNameWidgetProvider")
    suspend fun updateRecoveryByClassNameProviderWidgetSuspend(PackageName: String, ClassNameWidgetProvider: String, AddedWidgetRecovery: Boolean): Int


    @Query("DELETE FROM WidgetData WHERE PackageName = :PackageName AND ClassNameProvider = :ClassNameWidgetProvider")
    suspend fun deleteByWidgetClassNameProviderWidgetSuspend(PackageName: String, ClassNameWidgetProvider: String)

    @Query("SELECT COUNT(WidgetNumber) FROM WidgetData")
    suspend fun getRowCountSuspend(): Int
}