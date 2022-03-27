package com.palette.done.data.db.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.palette.done.data.db.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DoneDAO {
    // 던 ------------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDone(done: Done)

    @Query("DELETE FROM Done WHERE doneId = :doneNo")
    suspend fun deleteDone(doneNo: Int)

    @Query("SELECT * FROM Done WHERE date = :date ORDER BY doneId ASC")
    fun getAllDoneInDate(date: String): Flow<List<Done>>

    // 모든 던리스트
    @Query("SELECT * FROM Done")
    fun getAllDone(): Flow<List<Done>>

    // 해당 달의 모든 던리스트 수
    @Query("SELECT COUNT(*) FROM Done WHERE date LIKE :yearMonth || '%'")
    fun getAllDoneCountInMonth(yearMonth: String): Flow<Int>

    // 모든 각 날의 던리스트 개수
    @Query("SELECT date, COUNT(*) as count FROM DONE GROUP BY date")
    fun getDoneCountEachDayInMonth(): Flow<List<DoneCount>>

    // 해당 날의 던리스트 개수
    @Query("SELECT COUNT(*) FROM Done WHERE date = :date")
    suspend fun getAllDoneCountInDate(date: String): Int

    // 던리스트 작성일 수
    @Query("SELECT COUNT(*) FROM Done GROUP BY date")
    suspend fun getDoneWriteDays(): Int

    // 던리스트 총 개수
    @Query("SELECT COUNT(*) FROM Done")
    suspend fun getAllDoneCount(): Int

    // 플랜 -----------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: Plan)

    @Query("DELETE FROM `Plan` WHERE planNo = :planNo")
    suspend fun deletePlan(planNo: Int)

    @Query("SELECT * FROM `Plan` ORDER BY planNo")
    fun getAllPlan(): Flow<List<Plan>>

    // 루틴 -----------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine)

    @Query("DELETE FROM Routine WHERE routineNo = :routineNo")
    suspend fun deleteRoutine(routineNo: Int)

    @Query("SELECT * FROM Routine ORDER BY routineNo")
    fun getAllRoutine(): Flow<List<Routine>>

    // 오늘 한마디 -----------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodayRecord(todayRecord: TodayRecord)

    @Query("DELETE FROM TodayRecord WHERE todayNo = :todayNo")
    suspend fun deleteTodayRecord(todayNo: Int)

    @Query("SELECT * FROM TodayRecord WHERE date = :date")
    fun getTodayRecord(date: String): Flow<TodayRecord>

    // 카테고리 --------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT COUNT(*) FROM Category")
    suspend fun getCategoryCount(): Int

    @Query("SELECT * FROM Category")
    fun getCategory(): Flow<List<Category>>


}