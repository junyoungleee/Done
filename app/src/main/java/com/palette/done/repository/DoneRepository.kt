package com.palette.done.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.palette.done.data.db.dao.DoneDAO
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.entity.Plan
import com.palette.done.data.db.entity.Routine
import kotlinx.coroutines.flow.Flow

class DoneRepository(private val doneDao: DoneDAO) {

    // 던리스트 ----------------------------------------------------------------------
    suspend fun insertDone(done: Done) {
        doneDao.insertDone(done)
    }

    suspend fun deleteDone(done: Done) {
        doneDao.deleteDone(done)
    }

    fun getAllDoneInDate(date: String): Flow<List<Done>> {
        return doneDao.getAllDoneInDate(date)
    }

    fun getAllDone(): Flow<List<Done>> {
        return doneDao.getAllDone()
    }

    suspend fun getAllDoneCountInMonth(date: String): Int {
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
    fun getAllRoutine(): Flow<List<Routine>>{
        return doneDao.getAllRoutine()
    }

}