package com.palette.done.viewmodel

import androidx.lifecycle.*
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.datasource.StickerRepository
import com.palette.done.data.db.entity.Sticker
import com.palette.done.data.remote.repository.DoneServerRepository

class TodayStickerViewModel(val dbRepo: StickerRepository): ViewModel() {

    var _stickerId: MutableLiveData<Int> = MutableLiveData(0)
    val stickerId: LiveData<Int> get() = _stickerId

    val gainedSticker: LiveData<List<Sticker>> = dbRepo.getGainedSticker().asLiveData()

    fun getTodaySticker(): Int? {
        return if(stickerId.value == 0 || stickerId.value == null) {
            null
        } else {
            stickerId.value
        }
    }

    fun setTodaySticker(id: Int?) {
        if (id == null) {
            _stickerId.value = 0
        } else {
            _stickerId.value = id!!
        }
    }

    fun initStickerId() {
        _stickerId.value = 0
    }
}

class TodayStickerViewModelFactory(private val dbRepo: StickerRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodayStickerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodayStickerViewModel(dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}