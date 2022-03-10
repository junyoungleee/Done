package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.data.db.entity.Plan
import com.palette.done.data.remote.model.dones.Plans
import com.palette.done.data.remote.model.dones.PlansResponse
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.remote.repository.DoneServerRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlanViewModel(private val serverRepo: DoneServerRepository,
                    private val dbRepo: DoneRepository
) : ViewModel() {

    lateinit var selectedEditPlan: Plan

    var planList: LiveData<List<Plan>> = dbRepo.getAllPlan().asLiveData()

    private fun insertOrUpdatePlanInDB(plan: Plan) = viewModelScope.launch {
        dbRepo.insertPlan(plan)
    }

    private fun deletePlanInDB(planNo: Int) = viewModelScope.launch {
        dbRepo.deletePlan(planNo)
    }

    fun insertPlan(content: String, category: Int?) {
        viewModelScope.launch {
            serverRepo.postPlan(Plans(content, category))
                .enqueue(object : Callback<PlansResponse> {
                    override fun onResponse(call: Call<PlansResponse>, response: Response<PlansResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val planNo = response.body()!!.item!!.plan_no
                                    val plan = Plan(planNo, content, category)
                                    insertOrUpdatePlanInDB(plan)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<PlansResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun deletePlan(planNo: Int) {
        viewModelScope.launch {
            serverRepo.deletePlan(planNo)
                .enqueue(object : Callback<PlansResponse> {
                    override fun onResponse(call: Call<PlansResponse>, response: Response<PlansResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    deletePlanInDB(planNo)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<PlansResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun updatePlan(planNo: Int, content: String, category: Int?) {
        viewModelScope.launch {
            val new = Plans(content, category)
            serverRepo.patchPlan(new, planNo)
                .enqueue(object : Callback<PlansResponse> {
                    override fun onResponse(call: Call<PlansResponse>, response: Response<PlansResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val plan = Plan(planNo, content, category)
                                    insertOrUpdatePlanInDB(plan)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<PlansResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }
}

class PlanViewModelFactory(private val serverRepo: DoneServerRepository,
                           private val dbRepo: DoneRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlanViewModel(serverRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}