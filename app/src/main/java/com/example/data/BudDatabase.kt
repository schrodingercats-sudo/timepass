package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BudProfile::class], version = 1, exportSchema = false)
abstract class BudDatabase : RoomDatabase() {
    abstract fun budProfileDao(): BudProfileDao

    companion object {
        @Volatile
        private var INSTANCE: BudDatabase? = null

        fun getDatabase(context: Context): BudDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudDatabase::class.java,
                    "budcontrol_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
