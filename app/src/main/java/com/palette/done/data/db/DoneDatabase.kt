package com.palette.done.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.palette.done.data.db.dao.DoneDAO
import com.palette.done.data.db.entity.*
import kotlinx.coroutines.CoroutineScope

/**
 * <doneDB>
 * Done, TodayRecord, Plan, Routine 관리 DB
 */
@Database(entities = [Done::class, Plan::class, Routine::class, TodayRecord::class, Category::class], version = 3)
abstract class DoneDatabase: RoomDatabase() {
    abstract fun doneDao(): DoneDAO

    companion object {
        private var INSTANCE: DoneDatabase? = null
        fun getDatabase(context: Context): DoneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DoneDatabase::class.java, "doneDB" )
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Done ADD COLUMN 'tagNo' INTEGER")
                database.execSQL("ALTER TABLE Done ADD COLUMN 'routineNo' INTEGER")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE Category ('categoryNo' INTEGER NOT NULL, 'name' TEXT NOT NULL, PRIMARY KEY('categoryNo'))")
            }
        }
    }

}