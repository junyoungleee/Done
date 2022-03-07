package com.palette.done.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.palette.done.data.db.dao.DoneDAO
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.entity.Plan
import com.palette.done.data.db.entity.Routine
import com.palette.done.data.db.entity.TodayRecord
import kotlinx.coroutines.CoroutineScope

/**
 * <doneDB>
 * Done, TodayRecord, Plan, Routine 관리 DB
 */
@Database(entities = [Done::class, Plan::class, Routine::class, TodayRecord::class], version = 1)
abstract class DoneDatabase: RoomDatabase() {
    abstract fun doneDao(): DoneDAO

    companion object {
        private var INSTANCE: DoneDatabase? = null
        fun getDatabase(context: Context, scope: CoroutineScope): DoneDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DoneDatabase::class.java, "doneDB" )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}