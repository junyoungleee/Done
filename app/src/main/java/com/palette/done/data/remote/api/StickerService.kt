package com.palette.done.data.remote.api

import com.palette.done.data.remote.model.sticker.StickerResponse
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface StickerService {

    @GET("/api/stickers")
    fun getAllSticker(): Call<StickerResponse>

    @GET("/api/profile/stickers")
    fun getAllGainedSticker(): Call<StickerResponse>

    @POST("/api/stickers/{stickerNo}")
    fun postGainedSticker(@Path("stickerNo") stickerNo: Int): Call<StickerResponse>

}