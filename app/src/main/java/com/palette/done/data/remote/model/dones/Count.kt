package com.palette.done.data.remote.model.dones

data class DateCount(
    val date: String,
    val count: Int
)

data class DateCountTotal(
    val total_count: Int,
    val date_details: List<DateCount>
)

data class CountResponse(
    val item: DateCountTotal?,
    val message: String?,
    val is_success: Boolean
)