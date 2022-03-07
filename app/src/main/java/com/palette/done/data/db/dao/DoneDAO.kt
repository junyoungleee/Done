package com.palette.done.data.db.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.entity.Plan
import com.palette.done.data.db.entity.Routine
import com.palette.done.data.db.entity.TodayRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DoneDAO {
    // 던 ------------------------------------------------------------------------------------------
    @Insert
    suspend fun insertDone(done: Done)

    @Delete
    suspend fun deleteDone(done: Done)

    @Update
    suspend fun updateDone(done: Done)

    @Query("SELECT * FROM Done WHERE date = :date ORDER BY doneId ASC")
    fun getAllDoneInDate(date: String): Flow<List<Done>>

    @Query("SELECT * FROM Done")
    fun getAllDone(): Flow<List<Done>>

    @Query("SELECT COUNT(*) FROM Done WHERE date LIKE :yearMonth")
    fun getAllDoneCountInMonth(yearMonth: String): Int

    @Query("SELECT COUNT(*) FROM Done WHERE date = :date")
    fun getAllDoneCountInDate(date: String): Int

    // 플랜 -----------------------------------------------------------------------------------------
    @Insert
    suspend fun insertPlan(plan: Plan)

    @Delete
    suspend fun deletePlan(plan: Plan)

    @Update
    suspend fun updatePlan(plan: Plan)

    @Query("SELECT * FROM `Plan` ORDER BY planNo")
    fun getAllPlan(): LiveData<List<Plan>>

    // 루틴 -----------------------------------------------------------------------------------------
    @Insert
    suspend fun insertRoutine(routine: Routine)

    @Delete
    suspend fun deleteRoutine(routine: Routine)

    @Update
    suspend fun updateRoutine(routine: Routine)

    @Query("SELECT * FROM Routine ORDER BY routineNo")
    fun getAllRoutine(): LiveData<List<Routine>>

    // 오늘 한마디 -----------------------------------------------------------------------------------
    @Insert
    suspend fun insertTodayRecord(todayRecord: TodayRecord)

    @Delete
    suspend fun deleteTodayRecord(todayRecord: TodayRecord)

    @Update
    suspend fun updateTodayRecord(todayRecord: TodayRecord)

    @Query("SELECT * FROM TodayRecord WHERE date = :date")
    fun getTodayRecord(date: String): LiveData<TodayRecord>
}