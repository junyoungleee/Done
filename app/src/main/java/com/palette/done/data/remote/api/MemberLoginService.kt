package com.palette.done.data.remote.api

import com.palette.done.data.remote.model.member.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MemberLoginService {

    @POST("/api/auth/check-email")
    fun postEmailCheck(@Body data: CheckEmail): Call<CheckEmailResponse>

    @POST("/api/auth/login")
    fun postLogin(@Body data: MemberAccount): Call<MemberLoginResponse>

    @POST("/api/auth/signup")
    fun postSignUp(@Body data: MemberAccount): Call<MemberSignUpResponse>

    @POST("/api/auth/password")
    fun postPwdEmail(@Body data: CheckEmail): Call<CheckEmailResponse>

}
