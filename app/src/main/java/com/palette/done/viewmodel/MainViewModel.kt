package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.entity.DoneCount
import com.palette.done.data.remote.repository.DoneServerRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collector
import java.util.stream.Collectors

class MainViewModel(private val serverRepo: DoneServerRepository,
                    private val dbRepo: DoneRepository
) : ViewModel() {

    // 이번달 초기화
    val format = SimpleDateFormat("yyyy-MM")
    val today = format.format(Calendar.getInstance().time)

    var _yearMonth: MutableLiveData<String> = MutableLiveData(today)
    val yearMonth: LiveData<String> get() = _yearMonth
    var doneCount: LiveData<Int> = Transformations.switchMap(yearMonth){
        dbRepo.getAllDoneCountInMonth(it).asLiveData()
    }
    var doneCountList: LiveData<List<DoneCount>> = dbRepo.getDoneCountCountEachDayInMonth().asLiveData()
    private var doneCountMap: Map<String, Int> = mapOf()

    private fun doneCountListToMap() {
        Log.d("done_map", "1")
        val countList = doneCountList.value!!
        doneCountMap = countList.stream().collect(Collectors.toMap(DoneCount::date, DoneCount::count))
    }

    fun getDoneCountMap(): Map<String, Int> {
        doneCountListToMap()
        Log.d("done_map", "2")
        return doneCountMap
    }
}

class MainViewModelFactory(private val serverRepo: DoneServerRepository,
                               private val dbRepo: DoneRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(serverRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}