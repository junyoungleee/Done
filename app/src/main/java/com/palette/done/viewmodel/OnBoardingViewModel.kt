package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.DoneApplication
import com.palette.done.data.remote.model.member.MemberProfile
import com.palette.done.data.remote.model.member.MemberProfileResponse
import com.palette.done.data.remote.repository.MemberRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnBoardingViewModel(private val repository: MemberRepository): ViewModel() {

    var nickname: MutableLiveData<String> = MutableLiveData("")
    var userType: MutableLiveData<String> = MutableLiveData("")  // p or j
    var alarmHour: MutableLiveData<Int> = MutableLiveData(0)
    var alarmMin: MutableLiveData<Int> = MutableLiveData(0)  // 16:00 <- 24시 기준

    val weekList = mutableListOf<Int>()
    val _weekList = MutableLiveData<List<Int>>()
    var alarmWeekday: LiveData<List<Int>> = _weekList

    var patchSuccess: MutableLiveData<Boolean> = MutableLiveData(false)

    val week = arrayListOf<String>("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

    fun patchMemberProfile() {
//        val alarmTime = "${alarmHour.value}:${alarmMin.value}"
//
//        // "Mon,Tue" <- 요일 추가시 ,붙임
//        val selectedWeek = alarmWeekday.value!!.sorted()
//        var alarmCycle = ""
//        for(i in selectedWeek.indices) {
//            alarmCycle += week[selectedWeek[i]]+","
//        }
//        alarmCycle = alarmCycle.substring(0 until alarmCycle.length-1)
//        Log.d("ob_vm_cycle", "$alarmCycle")
//
//        val profile = MemberProfile(nickname.value.toString(), userType.value.toString(), alarmTime, alarmCycle)

        val profile = MemberProfile(nickname.value.toString(), userType.value.toString(), "", "")

        with(DoneApplication.pref) {
            nickname = this@OnBoardingViewModel.nickname.value.toString()
            type = this@OnBoardingViewModel.userType.value.toString()
//            this.alarmTime = alarmTime
//            this.alarmCycle = alarmCycle
        }

        viewModelScope.launch {
            repository.patchMemberProfile(profile).enqueue(object : Callback<MemberProfileResponse> {
                override fun onResponse(call: Call<MemberProfileResponse>, response: Response<MemberProfileResponse>) {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                patchSuccess.value = true
                                DoneApplication.pref.signup = "true"
                                Log.d("retrofit_200", "${response.body()!!.is_success}")
                            }
                            400 -> {
                                patchSuccess.value = false
                                Log.d("retrofit_400", "${response.body()!!.message}")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<MemberProfileResponse>, t: Throwable) {
                    Log.d("retrofit_failure", "${t.message}")
                }

            })
        }
    }

    fun setButtonSelectedAction(selected: Boolean, position: Int) {
        if (selected) {
            weekList.add(position)
        } else {
            weekList.remove(position)
        }
        _weekList.value = weekList
        Log.d("ob_vm_weekList", "${weekList}")
    }
}

class OnBoardingViewModelFactory(private val repository: MemberRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnBoardingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnBoardingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}