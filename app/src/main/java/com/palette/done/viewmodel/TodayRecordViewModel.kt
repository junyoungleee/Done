package com.palette.done.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.*
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.entity.TodayRecord
import com.palette.done.data.remote.model.dones.DonesResponse
import com.palette.done.data.remote.model.dones.TodayRecords
import com.palette.done.data.remote.model.dones.TodayRecordsResponse
import com.palette.done.data.remote.model.dones.TodayRecordsUpdate
import com.palette.done.data.remote.repository.DoneServerRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodayRecordViewModel (private val date: String,
                            private val serverRepo: DoneServerRepository,
                            private val dbRepo: DoneRepository
) : ViewModel() {

    var _todayText: MutableLiveData<String> = MutableLiveData("")
    val todayText: LiveData<String> get() = _todayText

    var _isSaved: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSaved: LiveData<Boolean> get() = _isSaved

    val todayRecord: LiveData<TodayRecord> = dbRepo.getTodayRecord(date).asLiveData()

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

    fun getTodayText(): String? {
        return if(todayText.value == "" || todayText.value == null) {
            null
        } else {
            todayText.value
        }
    }

    fun getTodayRecordId(): Int? {
        return if(todayRecord.value == null) {
            null
        } else {
            todayRecord.value!!.todayNo
        }
    }

    fun isExistingEdited(sticker: Int?): Boolean {
        Log.d("today_edit_vm", "${todayRecord.value!!.todayWord} - ${todayText.value}")
        return (todayRecord.value!!.todayWord != todayText.value) ||
            (todayRecord.value!!.todaySticker != sticker)
    }

    private fun insertOrUpdateTRinDB(todayRecord: TodayRecord) = viewModelScope.launch {
        dbRepo.insertTodayRecord(todayRecord)
        _isSaved.value = true
    }

    fun postTodayRecord(date: String, content: String?, sticker: Int?) {
        viewModelScope.launch {
            serverRepo.postTodayRecord(TodayRecords(date, content, sticker))
                .enqueue(object : Callback<TodayRecordsResponse> {
                    override fun onResponse(call: Call<TodayRecordsResponse>, response: Response<TodayRecordsResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val todayNo = response.body()!!.item!!.today_no
                                    val today = TodayRecord(todayNo, date, content, sticker)
                                    insertOrUpdateTRinDB(today)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<TodayRecordsResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun patchTodayRecord(date: String, todayNo: Int, content: String?, sticker: Int?) {
        viewModelScope.launch {
            serverRepo.patchTodayRecord(TodayRecordsUpdate(content, sticker), todayNo)
                .enqueue(object : Callback<TodayRecordsResponse> {
                    override fun onResponse(
                        call: Call<TodayRecordsResponse>,
                        response: Response<TodayRecordsResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val today = TodayRecord(todayNo, date, content, sticker)
                                    insertOrUpdateTRinDB(today)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<TodayRecordsResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }
}

class TodayRecordViewModelFactory(private val date: String,
                                  private val serverRepo: DoneServerRepository,
                                  private val dbRepo: DoneRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodayRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodayRecordViewModel(date, serverRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}