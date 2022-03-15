package com.palette.done.data.db.entity

import androidx.room.PrimaryKey

data class Member(
    @PrimaryKey
    var member_no: Int,
    var email: String,
    var password: String,
    var authority: String = "basic",
    var type: String = "p",
    var nickname: String?,
    var isRecvAlarm: Boolean = true
)
