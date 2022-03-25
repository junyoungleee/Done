package com.palette.done.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sticker(
    @PrimaryKey
    var stickerNo: Int,
    var name: String,
    var explanation: String,
    var term: String,
    var classify: Int,
    @ColumnInfo(name = "get", defaultValue = "0")
    var get: Boolean = false
)
