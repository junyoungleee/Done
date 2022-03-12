package com.palette.done.data.db.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.palette.done.data.db.dao.DoneDAO
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.entity.DoneCount
import com.palette.done.data.db.entity.Plan
import com.palette.done.data.db.entity.Routine
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
        return doneDao.getDoneCountCountEachDayInMonth()
    }

    fun getAllDoneCountInMonth(date: String): Flow<Int> {
        return doneDao.getAllDoneCountInMonth(date)
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

}