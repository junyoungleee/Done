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

data class MemberTypeResponse(
    val item: MemberType?,
    val message: String?,
    val is_success: Boolean
)

data class MemberType(
    val type: String
)

data class MemberAlarmResponse(
    val item: MemberAlarm,
    val message: String?,
    val is_success: Boolean
)

data class MemberAlarm(
    val alarm_time: String,
    val alarm_cycle: String,
    val is_recv_alarm: Boolean
)

data class MemberInfo(
    val nickname: String,
    val level: Int,
    val level_message: String,
    val total_done_count: Int,
    val plan_achievement_rate: Int,
    val this_month_done_count: Int
)

data class MemberInfoResponse(
    val item: MemberInfo?,
    val is_success: Boolean,
    val message: String?
)