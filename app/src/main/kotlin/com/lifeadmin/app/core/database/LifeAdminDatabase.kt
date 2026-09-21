package com.lifeadmin.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lifeadmin.app.core.database.dao.CategoryDao
import com.lifeadmin.app.core.database.dao.ReminderDao
import com.lifeadmin.app.data.local.DefaultCategories
import com.lifeadmin.app.data.local.entity.CategoryEntity
import com.lifeadmin.app.data.local.entity.ReminderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Room database for Life Admin
 * Version 1: Initial schema
 * Version 2: Added notes field to reminders table
 */
@Database(
    entities = [
        ReminderEntity::class,
        CategoryEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class LifeAdminDatabase : RoomDatabase() {
    
    abstract fun reminderDao(): ReminderDao
    abstract fun categoryDao(): CategoryDao
    
    companion object {
        private const val DATABASE_NAME = "life_admin.db"
        
        @Volatile
        private var INSTANCE: LifeAdminDatabase? = null
        
        /**
         * Migration from version 1 to version 2: Add notes column
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE reminders ADD COLUMN notes TEXT")
            }
        }
        
        fun getInstance(context: Context): LifeAdminDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        
        private fun buildDatabase(context: Context): LifeAdminDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                LifeAdminDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .addCallback(DatabaseCallback())
                .build()
        }
        
        /**
         * Database callback to populate default categories on first launch
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                
                // Populate default categories on first launch
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDefaultCategories(database.categoryDao())
                    }
                }
            }
            
            private suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
                val defaultCategories = DefaultCategories.getDefaultCategories()
                categoryDao.insertAll(defaultCategories)
            }
        }
    }
}
