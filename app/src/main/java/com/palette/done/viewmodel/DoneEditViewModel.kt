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

    fun addDoneInDB(done: Done) = viewModelScope.launch {
        dbRepo.insertDone(done)
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
                                    val done = Done(doneId, date, content, category)
                                    addDoneInDB(done)
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