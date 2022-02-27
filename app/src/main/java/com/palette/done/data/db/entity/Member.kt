package com.palette.done.data.db.entity

data class Member(
    val member_no: Int,
    val email: String,
    val password: String,
    val authority: String = "basic",
    val type: String = "p",
    var nickname: String?,
    val isRecvAlarm: Boolean = true
)
