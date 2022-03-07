package com.palette.done.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.palette.done.data.db.dao.DoneDAO
import com.palette.done.data.db.entity.Done
import kotlinx.coroutines.flow.Flow

class DoneRepository(private val doneDao: DoneDAO) {

    suspend fun insertDone(done: Done) {
        doneDao.insertDone(done)
    }

    suspend fun deleteDone(done: Done) {
        doneDao.deleteDone(done)
    }

    suspend fun updateDone(done: Done) {
        doneDao.updateDone(done)
    }

    fun getAllDoneInDate(date: String): Flow<List<Done>> {
        return doneDao.getAllDoneInDate(date)
    }

    fun getAllDone(): Flow<List<Done>> {
        return doneDao.getAllDone()
    }

    fun getAllDoneCountInMonth(date: String): Int {
        return doneDao.getAllDoneCountInMonth(date)
    }

}