package com.palette.done.data.remote.api

import com.palette.done.data.remote.model.dones.*
import retrofit2.Call
import retrofit2.http.*

interface DoneService {

    @POST("/api/dones")
    fun postDones(@Body data: Dones): Call<DonesResponse>

    @POST("/api/dones/today-record")
    fun postTodayRecord(@Body data: TodayRecords): Call<TodayRecordsResponse>

    @PATCH("/api/dones/{doneNo}")
    fun patchDones(@Body data: Dones, @Path("doneNo") doneNo: Int): Call<DonesResponse>

    @DELETE("/api/dones/{doneNo}")
    fun deleteDones(@Path("doneNo") doneNo: Int): Call<DonesResponse>


    // 태그 -----------------------------------------------------------------------------------------

    @GET("/api/tags")
    fun getHashTags(): Call<TagsResponse>
}