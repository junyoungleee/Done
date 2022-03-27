package com.palette.done.data.remote.repository

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

    fun postEmailPwd(email: CheckEmail): Call<CheckEmailResponse> {
        return loginApi.postPwdEmail(email)
    }

    // 유저 정보
    fun getMemberInfo(): Call<MemberInfoResponse> {
        return profileApi.getMemberInfo()
    }

    fun patchMemberProfile(profile: MemberProfile): Call<MemberProfileResponse> {
        return profileApi.patchMemberProfile(profile)
    }

    // 유저 정보 수정
    fun patchNewNickName(new: MemberNickname): Call<MemberProfileResponse> {
        return profileApi.patchMemberNickname(new)
    }

    fun patchNewType(): Call<MemberTypeResponse> {
        return profileApi.patchMemberType()
    }

    fun patchNewPwd(new: MemberPwd): Call<MemberChangePwdResponse> {
        return profileApi.patchMemberPwd(new)
    }

}