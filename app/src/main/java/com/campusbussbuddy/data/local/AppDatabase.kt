package com.campusbussbuddy.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.campusbussbuddy.data.local.dao.BusDao
import com.campusbussbuddy.data.local.dao.UserDao
import com.campusbussbuddy.data.local.entity.BusEntity
import com.campusbussbuddy.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, BusEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun busDao(): BusDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campus_bus_buddy_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}