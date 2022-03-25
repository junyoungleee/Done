package com.palette.done.data.remote.repository

import com.palette.done.data.remote.ApiBuilder
import com.palette.done.data.remote.api.StickerService
import com.palette.done.data.remote.model.sticker.StickerResponse
import retrofit2.Call

class StickerServerRepository {

    private val stickerApi: StickerService by lazy {
        ApiBuilder.retrofit.create(StickerService::class.java)
    }

    fun getAllSticker(): Call<StickerResponse> {
        return stickerApi.getAllSticker()
    }

    fun getAllGainedSticker(): Call<StickerResponse> {
        return stickerApi.getAllGainedSticker()
    }

    fun postGainedSticker(stickerNo: Int): Call<StickerResponse> {
        return stickerApi.postGainedSticker(stickerNo)
    }
}