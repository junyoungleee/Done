package com.palette.done.data.remote.repository

import com.palette.done.data.remote.ApiBuilder
import com.palette.done.data.remote.api.DoneService
import com.palette.done.data.remote.model.dones.*
import retrofit2.Call

class DoneServerRepository {

    private val doneApi: DoneService by lazy {
        ApiBuilder.retrofit.create(DoneService::class.java)
    }

    fun postDone(dones: Dones): Call<DonesResponse> {
        return doneApi.postDones(dones)
    }

    fun patchDone(dones: DonesUpdate, doneNo: Int): Call<DonesResponse> {
        return doneApi.patchDones(dones, doneNo)
    }

    fun deleteDone(doneNo: Int): Call<DonesResponse> {
        return doneApi.deleteDones(doneNo)
    }

    fun getDoneAndTodayRecord(date: String): Call<DonesAndTodayResponse> {
        return doneApi.getDoneListAndTodayRecord(date)
    }

    // 플랜 ----------------------------------------------------------------------------------------
    fun postPlan(plans: Plans): Call<PlansResponse> {
        return doneApi.postPlans(plans)
    }

    fun patchPlan(plans: Plans, planNo: Int): Call<PlansResponse> {
        return doneApi.patchPlans(plans, planNo)
    }

    fun deletePlan(planNo: Int): Call<PlansResponse> {
        return doneApi.deletePlans(planNo)
    }

    fun donePlan(planNo: Int): Call<DonesResponse> {
        return doneApi.donePlans(planNo)
    }

    fun getPlanList(): Call<PlanListResponse> {
        return doneApi.getPlans()
    }

    // 루틴 ----------------------------------------------------------------------------------------
    fun postRoutine(routines: Routines): Call<RoutinesResponse> {
        return doneApi.postRoutines(routines)
    }

    fun patchRoutines(routines: Routines, routineNo: Int): Call<RoutinesResponse> {
        return doneApi.patchRoutines(routines, routineNo)
    }

    fun deleteRoutines(routineNo: Int): Call<RoutinesResponse> {
        return doneApi.deleteRoutines(routineNo)
    }

    fun getRoutineList(): Call<RoutineListResponse> {
        return doneApi.getRoutines()
    }

    // 태그 ----------------------------------------------------------------------------------------
    fun getHashTag(): Call<TagsResponse> {
        return doneApi.getHashTags()
    }
}