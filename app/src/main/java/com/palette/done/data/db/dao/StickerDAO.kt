package com.palette.done.data.db.dao

import androidx.room.*
import com.palette.done.data.db.entity.Sticker
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSticker(sticker: Sticker)

    @Query("SELECT * FROM Sticker WHERE classify = :typeNum ORDER BY stickerNo ASC")
    fun getStickerInClassify(typeNum: Int): Flow<List<Sticker>>

    @Query("SELECT * FROM Sticker WHERE get = 1")
    fun getGainedSticker(): Flow<List<Sticker>>

    @Query("UPDATE Sticker SET get=1 WHERE stickerNo = :stickerNo")
    suspend fun updateGainSticker(stickerNo: Int)
}