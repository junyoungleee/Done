package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.data.db.datasource.StickerRepository
import com.palette.done.data.db.entity.Sticker
import com.palette.done.data.remote.model.sticker.StickerResponse
import com.palette.done.data.remote.model.sticker.Stickers
import com.palette.done.data.remote.repository.StickerServerRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainStickerViewModel (private val serverRepo: StickerServerRepository,
                            private val dbRepo: StickerRepository
) : ViewModel() {

    var _newStickers: MutableLiveData<List<Stickers>> = MutableLiveData(listOf())
    val newStickers: LiveData<List<Stickers>> get() = _newStickers

    fun updateGainedSticker(stickerNo: Int) {
        viewModelScope.launch {
            dbRepo.updateGainedSticker(stickerNo)
        }
    }

    fun insertSticker(sticker: Sticker) {
        viewModelScope.launch {
            dbRepo.insertSticker(sticker)
        }
    }

    fun getNewSticker() {
        viewModelScope.launch {
            serverRepo.getNewSticker()
                .enqueue(object : Callback<StickerResponse> {
                    override fun onResponse(call: Call<StickerResponse>, response: Response<StickerResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val newList =  response.body()!!.item?.stickers
                                    if (newList != null) {
                                        _newStickers.value = newList!!
                                    }
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<StickerResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun postGainedSticker(stickerNo: Int) {
        viewModelScope.launch {
            serverRepo.postGainedSticker(stickerNo)
                .enqueue(object : Callback<StickerResponse> {
                    override fun onResponse(call: Call<StickerResponse>, response: Response<StickerResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    updateGainedSticker(stickerNo)
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<StickerResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun getAllSticker() {
        viewModelScope.launch {
            serverRepo.getAllSticker()
                .enqueue(object : Callback<StickerResponse> {
                    override fun onResponse(call: Call<StickerResponse>, response: Response<StickerResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val list = response.body()!!.item!!.stickers
                                    for (s in list) {
                                        val classify = when(s.classify) {
                                            "공통" -> 1
                                            "나도 이제 프로기록러" -> 2
                                            "스페셜" -> 3
                                            else -> 0
                                        }
                                        val sticker = Sticker(s.sticker_no, s.name, s.explanation, s.term, classify)
                                        insertSticker(sticker)
                                    }
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<StickerResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }
}

class MainStickerViewModelFactory(private val serverRepo: StickerServerRepository,
                              private val dbRepo: StickerRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainStickerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainStickerViewModel(serverRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}