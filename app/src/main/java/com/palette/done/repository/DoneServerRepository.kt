package com.palette.done.repository

import com.palette.done.data.remote.ApiBuilder
import com.palette.done.data.remote.api.DoneService
import com.palette.done.data.remote.model.dones.Dones
import com.palette.done.data.remote.model.dones.DonesResponse
import retrofit2.Call
import retrofit2.create

class DoneServerRepository {

    private val doneApi: DoneService by lazy {
        ApiBuilder.retrofit.create(DoneService::class.java)
    }

    fun postDone(dones: Dones): Call<DonesResponse> {
        return doneApi.postDones(dones)
    }
}