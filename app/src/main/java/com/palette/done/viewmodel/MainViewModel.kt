package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.DoneApplication
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.entity.Category
import com.palette.done.data.db.entity.DoneCount
import com.palette.done.data.db.entity.Plan
import com.palette.done.data.remote.model.dones.CategoryResponse
import com.palette.done.data.remote.model.dones.PlanListResponse
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.view.main.DoneActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    init {
        getCategory()
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

    private fun insertOrUpdateCategoryInDB(category: Category) = viewModelScope.launch {
        dbRepo.insertCategory(category)
    }

    fun getCategory() {
        viewModelScope.launch {
            serverRepo.getCategories()
                .enqueue(object : Callback<CategoryResponse> {
                    override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val list = response.body()!!.item!!.categories
                                    for (c in list) {
                                        Log.d("category_server", "${c.name}")
                                        val category = Category(c.category_no, c.name)
                                        insertOrUpdateCategoryInDB(category)
                                    }
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
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