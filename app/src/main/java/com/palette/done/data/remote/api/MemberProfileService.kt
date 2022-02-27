package com.palette.done.data.remote.api

import com.palette.done.data.remote.model.member.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH

interface MemberProfileService {

    @PATCH("/api/profile")
    fun patchMemberProfile(@Body data: MemberProfile): Call<MemberProfileResponse>

    @PATCH("/api/profile/nickname")
    fun patchMemberNickname(@Body data: MemberNickname): Call<MemberProfileResponse>
}