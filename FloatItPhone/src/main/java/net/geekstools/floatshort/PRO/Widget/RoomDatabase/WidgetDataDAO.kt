/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 4:53 PM
 * Last modified 3/24/20 4:01 PM
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

    @Query("SELECT COUNT(WidgetNumber) FROM WidgetData")
    fun getRowCount(): Int












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