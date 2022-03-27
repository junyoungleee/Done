package com.palette.done.data.db.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.palette.done.data.db.dao.DoneDAO
import com.palette.done.data.db.entity.*
import kotlinx.coroutines.flow.Flow

class DoneRepository(private val doneDao: DoneDAO) {

    // 던리스트 ----------------------------------------------------------------------
    suspend fun insertDone(done: Done) {
        doneDao.insertDone(done)
    }

    suspend fun deleteDone(doneNo: Int) {
        doneDao.deleteDone(doneNo)
    }

    fun getAllDoneInDate(date: String): Flow<List<Done>> {
        return doneDao.getAllDoneInDate(date)
    }

    fun getDoneCountCountEachDayInMonth(): Flow<List<DoneCount>> {
        return doneDao.getDoneCountEachDayInMonth()
    }

    fun getAllDoneCountInMonth(date: String): Flow<Int> {
        return doneDao.getAllDoneCountInMonth(date)
    }

    suspend fun getDoneWriteDays(): Int {
        return doneDao.getDoneWriteDays()
    }

    suspend fun getAllDoneCount(): Int {
        return doneDao.getAllDoneCount()
    }

    // 오늘한마디 ---------------------------------------------------------------------
    suspend fun insertTodayRecord(today: TodayRecord) {
        doneDao.insertTodayRecord(today)
    }

    fun getTodayRecord(date: String): Flow<TodayRecord> {
        return doneDao.getTodayRecord(date)
    }

    // 플랜 --------------------------------------------------------------------------
    suspend fun insertPlan(plan: Plan) {
        doneDao.insertPlan(plan)
    }

    suspend fun deletePlan(planNo: Int) {
        doneDao.deletePlan(planNo)
    }

    fun getAllPlan(): Flow<List<Plan>>{
        return doneDao.getAllPlan()
    }

    // 루틴 --------------------------------------------------------------------------
    suspend fun insertRoutine(routine: Routine) {
        doneDao.insertRoutine(routine)
    }

    suspend fun deleteRoutine(routineNo: Int) {
        doneDao.deleteRoutine(routineNo)
    }

    fun getAllRoutine(): Flow<List<Routine>>{
        return doneDao.getAllRoutine()
    }

    // 카테고리 ------------------------------------------------------------------------
    suspend fun insertCategory(category: Category) {
        doneDao.insertCategory(category)
    }

    suspend fun getCountCategory(): Int {
        return doneDao.getCategoryCount()
    }

    fun getCategory(): Flow<List<Category>> {
        return doneDao.getCategory()
    }

}