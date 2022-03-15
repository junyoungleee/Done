package com.palette.done.data.remote.api

import com.palette.done.data.remote.model.dones.*
import retrofit2.Call
import retrofit2.http.*

interface DoneService {

    @GET("/api/categories")
    fun getCategories(): Call<CategoryResponse>

    // 던리스트 -------------------------------------------------------------------------------------
    @POST("/api/dones")
    fun postDones(@Body data: Dones): Call<DonesResponse>

    @PATCH("/api/dones/{doneNo}")
    fun patchDones(@Body data: DonesUpdate, @Path("doneNo") doneNo: Int): Call<DonesResponse>

    @DELETE("/api/dones/{doneNo}")
    fun deleteDones(@Path("doneNo") doneNo: Int): Call<DonesResponse>

    @GET("/api/dones")
    fun getDoneListAndTodayRecord(@Query("date") date: String): Call<DonesAndTodayResponse>

    // 오늘 한마디 -----------------------------------------------------------------------------------
    @POST("/api/dones/today-record")
    fun postTodayRecord(@Body data: TodayRecords): Call<TodayRecordsResponse>


    // 플랜 -----------------------------------------------------------------------------------------
    @POST("/api/plans")
    fun postPlans(@Body data: Plans): Call<PlansResponse>

    @PATCH("/api/plans/{planNo}")
    fun patchPlans(@Body data: Plans, @Path("planNo") planNo: Int): Call<PlansResponse>

    @DELETE("/api/plans/{planNo}")
    fun deletePlans(@Path("planNo") planNo: Int): Call<PlansResponse>

    @POST("/api/plans/{planNo}")
    fun donePlans(@Path("planNo") planNo: Int): Call<DonesResponse>

    @GET("/api/plans")
    fun getPlans(): Call<PlanListResponse>

    // 루틴 -----------------------------------------------------------------------------------------
    @POST("/api/routines")
    fun postRoutines(@Body data: Routines): Call<RoutinesResponse>

    @PATCH("/api/routines/{routineNo}")
    fun patchRoutines(@Body data: Routines, @Path("routineNo") routineNo: Int): Call<RoutinesResponse>

    @DELETE("/api/routines/{routineNo}")
    fun deleteRoutines(@Path("routineNo") routineNo: Int): Call<RoutinesResponse>

    @GET("/api/routines")
    fun getRoutines(): Call<RoutineListResponse>

    // 태그 -----------------------------------------------------------------------------------------
    @GET("/api/tags")
    fun getHashTags(): Call<TagsResponse>


}