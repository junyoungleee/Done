package com.palette.done.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.palette.done.repository.MemberRepository
import java.text.SimpleDateFormat
import java.util.*

class DoneDateViewModel() : ViewModel() {

    val format = SimpleDateFormat("yyyy-MM-dd")

    var calDate: MutableLiveData<Calendar> = MutableLiveData(Calendar.getInstance())
    var titleDate: MutableLiveData<String> = MutableLiveData("")

    fun setTitleDate(date: String) {
        var tDate: String = date
        tDate = tDate.replace("-", ".")
        tDate = tDate.substring(2)
        titleDate.value = tDate
        Log.d("date_vm", "${titleDate.value}")
    }

    fun transStringToCalendar(date: String) {
        val psDate = format.parse(date)
        val cal: Calendar = Calendar.getInstance()
        cal.time = psDate
        calDate.value = cal
        Log.d("date_cal", "${cal.time}")
    }

    fun transCalendarToString() {
        val date = calDate.value!!.time
        val strDate = format.format(date)
        setTitleDate(strDate)
    }

    fun clickedForwardDay() {
        calDate.value!!.add(Calendar.DATE, 1)
        transCalendarToString()
    }

    fun clickedBackwardDay() {
        calDate.value!!.add(Calendar.DATE, -1)
        transCalendarToString()
    }

}
