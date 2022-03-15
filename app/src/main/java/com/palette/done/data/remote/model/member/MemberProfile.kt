package com.palette.done.data.remote.model.member

import com.google.gson.annotations.SerializedName

data class MemberProfile(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("member_type")
    val memberType: String,
    @SerializedName("alarm_time")
    val alarmTime: String,
    @SerializedName("alarm_cycle")
    val alarmCycle: String
)

data class MemberNickname(
    @SerializedName("nickname")
    val nickname: String
)

data class MemberProfileResponse(
    val message: String?,
    val is_success: Boolean
)
