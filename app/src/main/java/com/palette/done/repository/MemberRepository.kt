package com.palette.done.repository

import com.palette.done.data.remote.ApiBuilder
import com.palette.done.data.remote.api.MemberLoginService
import com.palette.done.data.remote.api.MemberProfileService
import com.palette.done.data.remote.model.member.*
import retrofit2.Call
import com.palette.done.data.remote.model.member.MemberLoginResponse

/**
 * 멤버 회원가입, 로그인, 유저 정보 등록
 */
class MemberRepository {

    private val loginApi: MemberLoginService by lazy {
        ApiBuilder.retrofit.create(MemberLoginService::class.java)
    }

    private val profileApi: MemberProfileService by lazy {
        ApiBuilder.retrofit.create(MemberProfileService::class.java)
    }

    // 회원가입 / 로그인
    fun postEmailCheck(email: CheckEmail): Call<CheckEmailResponse> {
        return loginApi.postEmailCheck(email)
    }

    fun postMemberLogin(account: MemberAccount): Call<MemberLoginResponse> {
        return loginApi.postLogin(account)
    }

    fun postMemberSignUp(account: MemberAccount): Call<MemberSignUpResponse> {
        return loginApi.postSignUp(account)
    }

    // 유저 정보
    fun patchMemberProfile(profile: MemberProfile): Call<MemberProfileResponse> {
        return profileApi.patchMemberProfile(profile)
    }

}