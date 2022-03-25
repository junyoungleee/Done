package com.palette.done.data.remote.model.member

import com.google.gson.annotations.SerializedName

data class MemberAccount(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class MemberLoginResponse(
    val item: MemberLoginResponseItem?,
    val message: String?,
    val is_success: Boolean,
)

data class MemberLoginResponseItem(
    val access_token: String
)

data class MemberSignUpResponse(
    val message: String?,
    val is_success: Boolean
)

data class MemberPwd(
    @SerializedName("new_password")
    val newPassword: String
)

data class MemberChangePwdResponse(
    val is_success: Boolean,
    val message: String?
)