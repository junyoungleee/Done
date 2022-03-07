package com.palette.done.data.remote.model.dones

import com.google.gson.annotations.SerializedName

// 던리스트 ---------------------------------------------------------
data class Dones (
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("category_no")
    val categoryNo: Int?,
    @SerializedName("tag_no")
    val tagNo: Int?,
    @SerializedName("routine_no")
    val routineNo: Int?
    )

data class DoneNo (val done_no: Int)

data class DonesResponse(
    val item: DoneNo?,
    val message: String? = null,
    val is_success: Boolean)

// 오늘 한마디 ---------------------------------------------------------
data class TodayRecords (
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String
)

data class TodayRecordsResponse(
    val item: TodayNo?,
    val message: String?,
    val is_success: Boolean
)

data class TodayNo(val today_no: Int)

// 플랜 -----------------------------------------------------------------
data class Plans(
    @SerializedName("content")
    val content: String,
    @SerializedName("category_no")
    val category_no: Int?
)

data class PlansResponse(
    val item: PlansNo?,
    val message: String?,
    val is_success: Boolean
)

data class PlansNo(val plan_no: Int)

// 루틴 -----------------------------------------------------------------
data class Routines(
    @SerializedName("content")
    val content: String,
    @SerializedName("category_no")
    val category_no: Int?
)

data class RoutinesResponse(
    val item: RoutinesNo?,
    val message: String?,
    val is_success: Boolean
)

data class RoutinesNo(val routine_no: Int)
