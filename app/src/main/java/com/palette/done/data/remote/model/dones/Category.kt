package com.palette.done.data.remote.model.dones

data class Category (
    val category_no: Int,
    val name: String
)

data class CategoryResponse(
    val item: Categories?,
    val message: String?,
    val is_success: Boolean
)

data class Categories(
    val categories: List<Category>
)