package com.palette.done.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.*
import com.palette.done.data.db.entity.Done
import com.palette.done.data.remote.model.dones.Dones
import com.palette.done.data.remote.model.dones.DonesResponse
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.entity.Routine
import com.palette.done.data.remote.model.dones.DonesUpdate
import com.palette.done.data.remote.repository.DoneServerRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoneEditViewModel(private val serverRepo: DoneServerRepository,
                        private val dbRepo: DoneRepository
) : ViewModel() {

    var _done: MutableLiveData<String> = MutableLiveData("")
    val done: LiveData<String> get() = _done

    var oldDone = Done(0, "", "", null, null, null)
    var oldDoneIndex: Int = 0

    var _selectedRoutineTag: MutableLiveData<Routine> = MutableLiveData(Routine(1, "", null))
    val selectedRoutineTag: LiveData<Routine> get() = _selectedRoutineTag

    fun onDoneTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _done.value = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
                _done.value = s.toString()
            }
        }
    }

    private fun insertOrUpdateDoneInDB(done: Done) = viewModelScope.launch {
        dbRepo.insertDone(done)
    }

    private fun deleteDoneInDB(doneNo: Int) = viewModelScope.launch {
        dbRepo.deleteDone(doneNo)
    }

    fun addDoneList(date: String, content: String, category: Int?, tag: Int?, routine: Int?) {
        // room & server 저장
        viewModelScope.launch {
            serverRepo.postDone(Dones(content, date, category, tag, routine))
                .enqueue(object : Callback<DonesResponse> {
                    override fun onResponse(call: Call<DonesResponse>, response: Response<DonesResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val doneId = response.body()!!.item!!.done_no
                                    val done = Done(doneId, date, content, category, tag, routine)
                                    insertOrUpdateDoneInDB(done)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<DonesResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun deleteDoneList(doneNo: Int) {
        viewModelScope.launch {
            serverRepo.deleteDone(doneNo)
                .enqueue(object : Callback<DonesResponse> {
                    override fun onResponse(call: Call<DonesResponse>, response: Response<DonesResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    deleteDoneInDB(doneNo)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<DonesResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun updateDoneList(done: Done) {
        viewModelScope.launch {
            val old = DonesUpdate(done.content, done.categoryNo, done.tagNo, done.routineNo)
            serverRepo.patchDone(old, done.doneId)
                .enqueue(object : Callback<DonesResponse> {
                    override fun onResponse(call: Call<DonesResponse>, response: Response<DonesResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    updateDoneList(done)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<DonesResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }


}

class DoneEditViewModelFactory(private val serverRepo: DoneServerRepository,
                               private val dbRepo: DoneRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoneEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoneEditViewModel(serverRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}