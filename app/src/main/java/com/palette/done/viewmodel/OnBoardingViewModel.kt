package com.palette.done.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.palette.done.data.remote.model.member.MemberProfile

class OnBoardingViewModel: ViewModel() {

    var nickname: MutableLiveData<String> = MutableLiveData("")
    var userType: MutableLiveData<String> = MutableLiveData("")  // p or j
    var alarmAmPm: MutableLiveData<String> = MutableLiveData("")
    var alarmHour: MutableLiveData<Int> = MutableLiveData(0)
    var alarmMin: MutableLiveData<Int> = MutableLiveData(0)  // 16:00 <- 24시 기준
    var alarmWeekday: MutableLiveData<List<Int>> = MutableLiveData()  // "Mon,Tue" <- 요일 추가시 ,붙임


    val week = arrayListOf<String>("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

    fun patchMemberProfile() {
        val alarm24Hour = if (alarmAmPm.value.toString() == "오후") {
            (alarmHour.value?.plus(12)).toString()  // 오후 12시 + 12 = 24시
        } else {
            alarmHour.value.toString()
        }
        val alarmTime = alarm24Hour + ":" + alarmMin.value.toString()

        val alarmCycle = ""

        val profile = MemberProfile(nickname.value.toString(), userType.value.toString(), alarmTime, "")
    }
}