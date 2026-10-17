package com.namabiz.agrim.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SupplierEntity::class,
        DocumentEntity::class,
        ScanHistoryEntity::class,
        RfqEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun supplierDao(): SupplierDao
    abstract fun documentDao(): DocumentDao
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun rfqDao(): RfqDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agrim_directory.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
