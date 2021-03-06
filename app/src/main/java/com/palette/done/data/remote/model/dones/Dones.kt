package com.palette.done.data.remote.model.dones

import com.google.gson.annotations.SerializedName

// 던리스트 ----------------------------------------------------------
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

data class DonesUpdate(
    @SerializedName("content")
    val content: String,
    @SerializedName("category_no")
    val categoryNo: Int?,
    @SerializedName("tag_no")
    val tagNo: Int?,
    @SerializedName("routine_no")
    val routineNo: Int?
)

// 오늘 한마디 ----------------------------------------------------------
data class TodayRecords (
    @SerializedName("date")
    val date: String,
    @SerializedName("content")
    val content: String?,
    @SerializedName("sticker_no")
    val sticker_no: Int?
)

data class TodayRecordsUpdate(
    @SerializedName("content")
    val content: String?,
    @SerializedName("sticker_no")
    val sticker_no: Int?
)

data class TodayRecordsResponse(
    val item: TodayNo?,
    val message: String?,
    val is_success: Boolean
)

data class TodayNo(val today_no: Int)

// 던리스트 & 오늘 한마디 -------------------------------------------------
data class DonesAndTodayResponse(
    val item : AllDoneAndToday?,
    val message: String?,
    val is_success: Boolean
)

data class AllDoneAndToday(
    val all_dones: List<DonesAndToday>
)

data class DonesAndToday(
    val date: String,
    val dones: List<DoneServer>,
    val today_record: TodayRecord
)

data class DoneServer(
    val done_no: Int,
    var content: String,
    var category_no: Int?,
    var tag_no: Int?,
    var routine_no: Int?
)

data class TodayRecord(
    val today_no: Int,
    val content: String?,
    val sticker_no: Int?
)

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

data class PlanListResponse(
    val item: PlanList?,
    val message: String?,
    val is_success: Boolean
)

data class PlanList(val plans: List<PlanListItem>)

data class PlanListItem(
    val plan_no: Int,
    val content: String,
    val category_no: Int?
)

data class PlanDone(
    @SerializedName("date")
    val date: String)

// 루틴 -----------------------------------------------------------------
data class Routines(
    @SerializedName("content")
    val content: String,
    @SerializedName("category_no")
    val category_no: Int?
)

data class RoutineListResponse(
    val item: RoutineList?,
    val message: String?,
    val is_success: Boolean
)

data class RoutineList(val routines: List<RoutineListItem>)

data class RoutineListItem(
    val routine_no: Int,
    val content: String,
    val category_no: Int?
)

data class RoutinesResponse(
    val item: RoutinesNo?,
    val message: String?,
    val is_success: Boolean
)

data class RoutinesNo(val routine_no: Int)
