package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AgentLogEntity::class, GitTaskEntity::class, ServiceLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SasaDatabase : RoomDatabase() {
    abstract fun sasaDao(): SasaDao

    companion object {
        @Volatile
        private var INSTANCE: SasaDatabase? = null

        fun getDatabase(context: Context): SasaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SasaDatabase::class.java,
                    "sasa_agent_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
