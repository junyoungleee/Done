package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.datasource.DoneRepository
import java.text.SimpleDateFormat
import java.util.*

class DoneDateViewModel(val dbRepo: DoneRepository) : ViewModel() {

    val format = SimpleDateFormat("yyyy-MM-dd")

    var calendar: MutableLiveData<Calendar> = MutableLiveData(Calendar.getInstance())
    var calDate: MutableLiveData<String> = MutableLiveData("")  // YYYY-MM-DD
    var titleDate: MutableLiveData<String> = MutableLiveData("")  // YY.MM.DD

    var doneList: LiveData<List<Done>> = Transformations.switchMap(calDate) {
        dbRepo.getAllDoneInDate(it).asLiveData()
    }

    fun setTitleDate(date: String) {
        calDate.value = date

        var tDate = date
        tDate = tDate.replace("-", ".")
        tDate = tDate.substring(2)
        titleDate.value = tDate
        Log.d("date_vm", "${titleDate.value}")
        Log.d("date_list", "${doneList.value?.size}")
    }

    fun getTitleDate(): String {
        return calDate.value.toString()
    }

    fun transStringToCalendar(date: String) {
        val psDate = format.parse(date)
        val cal: Calendar = Calendar.getInstance()
        cal.time = psDate
        calendar.value = cal
        Log.d("date_cal", "${cal.time}")
    }

    fun transCalendarToString() {
        val date = calendar.value!!.time
        val strDate = format.format(date)
        calDate.value = strDate.toString()
        setTitleDate(strDate)
    }

    fun clickedForwardDay() {
        calendar.value!!.add(Calendar.DATE, 1)
        Log.d("date_forward", "${calendar.value!!.time}")
        transCalendarToString()
    }

    fun clickedBackwardDay() {
        calendar.value!!.add(Calendar.DATE, -1)
        Log.d("date_backward", "${calendar.value!!.time}")
        transCalendarToString()
    }
}

class DoneDateViewModelFactory(private val dbRepo: DoneRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoneDateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoneDateViewModel(dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
