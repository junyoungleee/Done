package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.data.db.entity.Routine
import com.palette.done.data.remote.model.dones.Routines
import com.palette.done.data.remote.model.dones.RoutinesResponse
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.remote.model.dones.RoutineListResponse
import com.palette.done.data.remote.repository.DoneServerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoutineViewModel (private val serverRepo: DoneServerRepository, private val dbRepo: DoneRepository) : ViewModel() {

    lateinit var selectedEditRoutine: Routine

    var routineList: LiveData<List<Routine>> = dbRepo.getAllRoutine().asLiveData()

    private fun insertOrUpdateRoutineInDB(routine: Routine) = viewModelScope.launch {
        Log.d("routine_insert", "${routine.routineNo}")
        dbRepo.insertRoutine(routine)
    }

    private fun deleteRoutineInDB(routineNo: Int) = viewModelScope.launch {
        dbRepo.deleteRoutine(routineNo)
    }

    fun initRoutine() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getRoutine()
            }
        }
    }

    private fun getRoutine() {
        viewModelScope.launch {
            serverRepo.getRoutineList()
                .enqueue(object : Callback<RoutineListResponse> {
                    override fun onResponse(call: Call<RoutineListResponse>, response: Response<RoutineListResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val list = response.body()!!.item!!.routines
                                    for (r in list) {
                                        Log.d("routine_server", "${r.routine_no}")
                                        val routine = Routine(r.routine_no!!, r.content, r.category_no)
                                        insertOrUpdateRoutineInDB(routine)
                                    }
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<RoutineListResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }


    fun insertRoutine(content: String, category: Int?) {
        viewModelScope.launch {
            serverRepo.postRoutine(Routines(content, category))
                .enqueue(object : Callback<RoutinesResponse> {
                    override fun onResponse(call: Call<RoutinesResponse>, response: Response<RoutinesResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val routineNo = response.body()!!.item!!.routine_no
                                    val routine = Routine(routineNo, content, category)
                                    insertOrUpdateRoutineInDB(routine)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<RoutinesResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun deleteRoutine(routineNo: Int) {
        viewModelScope.launch {
            serverRepo.deleteRoutines(routineNo)
                .enqueue(object : Callback<RoutinesResponse> {
                    override fun onResponse(call: Call<RoutinesResponse>, response: Response<RoutinesResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    deleteRoutineInDB(routineNo)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<RoutinesResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun updateRoutine(routineNo: Int, content: String, category: Int?) {
        viewModelScope.launch {
            val new = Routines(content, category)
            serverRepo.patchRoutines(new, routineNo)
                .enqueue(object : Callback<RoutinesResponse> {
                    override fun onResponse(call: Call<RoutinesResponse>, response: Response<RoutinesResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val routine = Routine(routineNo, content, category)
                                    insertOrUpdateRoutineInDB(routine)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<RoutinesResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }
}

class RoutineViewModelFactory(private val serverRepo: DoneServerRepository,
                              private val dbRepo: DoneRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutineViewModel(serverRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}