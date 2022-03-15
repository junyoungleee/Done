package com.palette.done.data.remote.model.dones

data class Tags(
    val tag_no: Int,
    val name: String,
    val category_no: Int
)

data class TagsResponse(
    val item: TagsList?,
    val is_success: Boolean,
    val message: String?
)

data class TagsList(
    val tags: List<Tags>
)
