package com.palette.done.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Done(
    @PrimaryKey
    var doneId: Int,
    var date: String,
    var content: String,
    var categoryNo: Int?
)

@Entity
data class Plan(
    @PrimaryKey
    var planNo: Int,
    var content: String,
    var categoryNo: Int?,
)

@Entity
data class Routine(
    @PrimaryKey
    var routineNo: Int,
    var content: String,
    var categoryNo: Int?,
)

@Entity
data class TodayRecord(
    @PrimaryKey
    var todayNo: Int,
    var date: String,
    var todayWord: String?,
    var todaySticker: Int?,
    var createdAt: String,
    var updatedAt: String
)
