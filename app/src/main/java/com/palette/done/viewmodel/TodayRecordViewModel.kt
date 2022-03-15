package com.palette.done.viewmodel

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.remote.repository.DoneServerRepository

class TodayRecordViewModel (private val serverRepo: DoneServerRepository,
                            private val dbRepo: DoneRepository
) : ViewModel() {

    var _stickerId: MutableLiveData<Int> = MutableLiveData(0)
    val stickerId: LiveData<Int> get() = _stickerId

    var _todayText: MutableLiveData<String> = MutableLiveData("")
    val todayText: LiveData<String> get() = _todayText

    fun onTodayRecordTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _todayText.value = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
                _todayText.value = s.toString()
            }
        }
    }

    fun initStickerId() {
        _stickerId.value = 0
    }
}

class TodayRecordViewModelFactory(private val serverRepo: DoneServerRepository,
                               private val dbRepo: DoneRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodayRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodayRecordViewModel(serverRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}