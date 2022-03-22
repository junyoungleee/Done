package com.palette.done.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodayStickerViewModel: ViewModel() {

    var _stickerId: MutableLiveData<Int> = MutableLiveData(0)
    val stickerId: LiveData<Int> get() = _stickerId

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