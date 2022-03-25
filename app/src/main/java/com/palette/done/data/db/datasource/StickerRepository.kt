package com.palette.done.data.db.datasource

import com.palette.done.data.db.dao.StickerDAO
import com.palette.done.data.db.entity.Sticker
import kotlinx.coroutines.flow.Flow

class StickerRepository(private val stickerDao: StickerDAO) {

    suspend fun insertSticker(sticker: Sticker) {
        stickerDao.insertSticker(sticker)
    }

    fun getGainedSticker(): Flow<List<Sticker>> {
        return stickerDao.getGainedSticker()
    }

    fun getStickerInClassify(type: Int): Flow<List<Sticker>> {
        return stickerDao.getStickerInClassify(type)
    }

    suspend fun updateGainedSticker(stickerNo: Int) {
        stickerDao.updateGainSticker(stickerNo)
    }
}

