package com.palette.done.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.palette.done.data.db.dao.DoneDAO
import com.palette.done.data.db.dao.StickerDAO
import com.palette.done.data.db.entity.*
import kotlinx.coroutines.CoroutineScope

/**
 * <doneDB>
 * Done, TodayRecord, Plan, Routine 관리 DB
 */
@Database(entities = [Done::class, Plan::class, Routine::class, TodayRecord::class, Category::class, Sticker::class], version = 5)
abstract class DoneDatabase: RoomDatabase() {
    abstract fun doneDao(): DoneDAO
    abstract fun stickerDao(): StickerDAO

    companion object {
        private var INSTANCE: DoneDatabase? = null
        fun getDatabase(context: Context): DoneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DoneDatabase::class.java, "doneDB" )
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
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
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE Today ('todayNo' INTEGER NOT NULL, 'date' TEXT NOT NULL, todayWord TEXT, todaySticker INTEGER, PRIMARY KEY('todayNo'))")
                database.execSQL("DROP TABLE TodayRecord")
                database.execSQL("ALTER TABLE Today RENAME TO TodayRecord")
            }
        }
        val MIGRATION_4_5 = object: Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE Sticker ('stickerNo' INTEGER NOT NULL, 'name' TEXT NOT NULL, 'explanation' TEXT NOT NULL, 'term' TEXT NOT NULL, 'classify' INTEGER NOT NULL, 'get' INTEGER NOT NULL DEFAULT 0, PRIMARY KEY('stickerNo'))")
            }
        }
    }

}