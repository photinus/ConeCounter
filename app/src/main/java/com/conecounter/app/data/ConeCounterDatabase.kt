package com.conecounter.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Kid::class, Scoop::class], version = 1, exportSchema = false)
abstract class ConeCounterDatabase : RoomDatabase() {
    abstract fun kidDao(): KidDao
    abstract fun scoopDao(): ScoopDao

    companion object {
        @Volatile
        private var INSTANCE: ConeCounterDatabase? = null

        fun getInstance(context: Context): ConeCounterDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ConeCounterDatabase::class.java,
                    "conecounter.db"
                ).build().also { INSTANCE = it }
            }
    }
}
