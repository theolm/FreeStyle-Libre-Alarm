package dev.theolm.freestyle_libre_alarm.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.theolm.freestyle_libre_alarm.data.local.database.dao.AlarmEventDao
import dev.theolm.freestyle_libre_alarm.data.local.database.dao.NotificationLogDao
import dev.theolm.freestyle_libre_alarm.data.local.database.entity.AlarmEventEntity
import dev.theolm.freestyle_libre_alarm.data.local.database.entity.NotificationLogEntity

@Database(
    entities = [AlarmEventEntity::class, NotificationLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class LibreDatabase : RoomDatabase() {
    abstract fun alarmEventDao(): AlarmEventDao
    abstract fun notificationLogDao(): NotificationLogDao

    companion object {
        @Volatile
        private var INSTANCE: LibreDatabase? = null

        fun getDatabase(context: Context): LibreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibreDatabase::class.java,
                    "libre_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}