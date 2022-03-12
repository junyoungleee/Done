package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.data.db.entity.Plan
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.entity.Routine
import com.palette.done.data.remote.model.dones.*
import com.palette.done.data.remote.repository.DoneServerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PlanViewModel(private val serverRepo: DoneServerRepository,
                    private val dbRepo: DoneRepository
) : ViewModel() {

    lateinit var selectedEditPlan: Plan

    var planList: LiveData<List<Plan>> = dbRepo.getAllPlan().asLiveData()

    fun initPlan() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getPlan()
            }
        }
    }

    private fun getPlan() {
        viewModelScope.launch {
            serverRepo.getPlanList()
                .enqueue(object : Callback<PlanListResponse> {
                    override fun onResponse(call: Call<PlanListResponse>, response: Response<PlanListResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val list = response.body()!!.item!!.plans
                                    for (p in list) {
                                        Log.d("routine_server", "${p.plan_no}")
                                        val plan = Plan(p.plan_no, p.content, p.category_no)
                                        insertOrUpdatePlanInDB(plan)
                                    }
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<PlanListResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    private fun insertOrUpdatePlanInDB(plan: Plan) = viewModelScope.launch {
        dbRepo.insertPlan(plan)
    }

    private fun deletePlanInDB(planNo: Int) = viewModelScope.launch {
        dbRepo.deletePlan(planNo)
    }

    private fun insertPlanAsDoneInDB(done: Done) = viewModelScope.launch {
        dbRepo.insertDone(done)
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

    fun donePlan(plan: Plan) {
        viewModelScope.launch {
            serverRepo.donePlan(plan.planNo)
                .enqueue(object : Callback<DonesResponse> {
                    override fun onResponse(call: Call<DonesResponse>, response: Response<DonesResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    // db에서 Plan 삭제
                                    deletePlanInDB(plan.planNo)

                                    val format = SimpleDateFormat("yyyy-MM-dd")
                                    val today = format.format(Calendar.getInstance().time)
                                    val done = Done(response.body()!!.item!!.done_no, today, plan.content, plan.categoryNo, null, null)
                                    insertPlanAsDoneInDB(done)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<DonesResponse>, t: Throwable) {
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