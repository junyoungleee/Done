package com.palette.done.data.remote.model.member

import com.google.gson.annotations.SerializedName

data class CheckEmail(
    @SerializedName("email")
    val email: String
)

data class CheckEmailResponse(
    val message: String? = null,
    val is_success: Boolean
)
