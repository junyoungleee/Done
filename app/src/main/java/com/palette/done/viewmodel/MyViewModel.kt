package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.DoneApplication
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.remote.model.member.MemberInfoResponse
import com.palette.done.data.remote.repository.MemberRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyViewModel(var serverRepo: MemberRepository,
                  var dbRepo: DoneRepository) : ViewModel() {

    var _doneCount: MutableLiveData<Int> = MutableLiveData(0)
    val doneCount: LiveData<Int> get() = _doneCount

    var _writeDays: MutableLiveData<Int> = MutableLiveData(0)
    val writeDays: LiveData<Int> get() = _writeDays

    var cumulatedData: MediatorLiveData<Int> = MediatorLiveData()

    init {
        viewModelScope.launch {
            _doneCount.value = dbRepo.getAllDoneCount()
            _writeDays.value = dbRepo.getDoneWriteDays()
        }
        cumulatedData.addSource(doneCount) {
            cumulatedData.value = getLeftData()
        }
        cumulatedData.addSource(writeDays) {
            cumulatedData.value = getLeftData()
        }
    }

    private fun getLeftData(): Int {
        return if (doneCount.value != null && writeDays.value != null) {
            doneCount.value!! + writeDays.value!!
        } else {
            0
        }
    }

    var _leftMessage: MutableLiveData<String> = MutableLiveData("")
    val leftMessage: LiveData<String> get() = _leftMessage

    var _planAchieved: MutableLiveData<Int> = MutableLiveData(0)
    val planAchieved: LiveData<Int> get() = _planAchieved

    var _thisMonthCount: MutableLiveData<Int> = MutableLiveData(0)
    val thisMonthCount: LiveData<Int> = _thisMonthCount

    fun getUserProfile() {
        viewModelScope.launch {
            serverRepo.getMemberInfo()
                .enqueue(object : Callback<MemberInfoResponse> {
                    override fun onResponse(call: Call<MemberInfoResponse>, response: Response<MemberInfoResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val info = response.body()!!.item!!
                                    DoneApplication.pref.level = info.level
                                    _leftMessage.value = info.level_message
                                    _planAchieved.value = info.plan_achievement_rate
                                    _thisMonthCount.value = info.this_month_done_count
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }
}

class MyViewModelFactory(private val serverRepo: MemberRepository,
                           private val dbRepo: DoneRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyViewModel(serverRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}