package com.palette.done.data.remote.api

import com.palette.done.data.remote.model.member.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH

interface MemberProfileService {

    @PATCH("/api/profile")
    fun patchMemberProfile(@Body data: MemberProfile): Call<MemberProfileResponse>

    @PATCH("/api/profile/nickname")
    fun patchMemberNickname(@Body data: MemberNickname): Call<MemberProfileResponse>

    @PATCH("/api/profile/type")
    fun patchMemberType(): Call<MemberTypeResponse>

    @PATCH("/api/profile/password")
    fun patchMemberPwd(@Body data: MemberPwd): Call<MemberChangePwdResponse>

    @GET("/api/profile")
    fun getMemberInfo(): Call<MemberInfoResponse>

    @GET("/api/profile/type")
    fun getMemberType(): Call<MemberTypeResponse>

    @GET("/api/profile/alarm")
    fun getMemberAlarm(): Call<MemberAlarmResponse>

    @DELETE("/api/profile")
    fun deleteMember(): Call<MemberDeleteResponse>
}