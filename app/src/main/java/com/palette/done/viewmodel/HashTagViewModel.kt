package com.palette.done.viewmodel

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.*
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.entity.Done
import com.palette.done.data.remote.model.dones.DonesResponse
import com.palette.done.data.remote.model.dones.Tags
import com.palette.done.data.remote.model.dones.TagsResponse
import com.palette.done.data.remote.repository.DoneServerRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HashTagViewModel (private val serverRepo: DoneServerRepository,
                        private val dbRepo: DoneRepository
) : ViewModel() {

    var _tags: MutableLiveData<List<Tags>> = MutableLiveData(listOf())
    val tags: LiveData<List<Tags>> get() = _tags

    fun get6Tags() {
        viewModelScope.launch {
            serverRepo.getHashTag()
                .enqueue(object : Callback<TagsResponse> {
                    override fun onResponse(call: Call<TagsResponse>, response: Response<TagsResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val tagList = response.body()!!.item!!.tags
                                    _tags.value = tagList
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<TagsResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }
}

class HashTagViewModelFactory(private val serverRepo: DoneServerRepository,
                           private val dbRepo: DoneRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HashTagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HashTagViewModel(serverRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}