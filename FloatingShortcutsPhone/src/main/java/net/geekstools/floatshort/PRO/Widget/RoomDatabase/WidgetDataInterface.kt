package net.geeksempire.chat.vicinity.Util.RoomSqLiteDatabase.UserInformation

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WidgetDataModel::class], version = 1)
abstract class WidgetDataInterface : RoomDatabase() {
    abstract fun initDataAccessObject(): WidgetDataDAO
}