package com.example.bootcounter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BootEvent::class], version = 1, exportSchema = false)
abstract class BootEventDatabase : RoomDatabase() {

    abstract fun bootEventDao(): BootEventDao

    companion object {
        @Volatile
        private var INSTANCE: BootEventDatabase? = null

        fun getDatabase(context: Context): BootEventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BootEventDatabase::class.java,
                    "boot_event_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}