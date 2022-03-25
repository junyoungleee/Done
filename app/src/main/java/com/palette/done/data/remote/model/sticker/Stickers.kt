package com.palette.done.data.remote.model.sticker


data class Stickers(
    val sticker_no: Int,
    val name: String,
    val explanation: String,
    val term: String,
    val classify: String
)

data class StickerList(
    val stickers: List<Stickers>
)

data class StickerResponse(
    val item: StickerList?,
    val is_success: Boolean,
    val message: String?
)
