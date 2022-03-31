package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.DoneApplication
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.datasource.StickerRepository
import com.palette.done.data.db.entity.Category
import com.palette.done.data.db.entity.Sticker
import com.palette.done.data.remote.model.member.MemberNickname
import com.palette.done.data.remote.model.member.MemberProfileResponse
import com.palette.done.data.remote.model.sticker.StickerResponse
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.data.remote.repository.StickerServerRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StickerViewModel (private val serverRepo: StickerServerRepository, private val dbRepo: StickerRepository) : ViewModel() {

    var type1Stickers: LiveData<List<Sticker>> = dbRepo.getStickerInClassify(1).asLiveData()
    var type2Stickers: LiveData<List<Sticker>> = dbRepo.getStickerInClassify(2).asLiveData()
    var type3Stickers: LiveData<List<Sticker>> = dbRepo.getStickerInClassify(3).asLiveData()

    private fun updateGainedSticker(stickerNo: Int) {
        viewModelScope.launch {
            dbRepo.updateGainedSticker(stickerNo)
        }
    }

    fun getAllGainedSticker() {
        viewModelScope.launch {
            serverRepo.getAllGainedSticker()
                .enqueue(object : Callback<StickerResponse> {
                    override fun onResponse(call: Call<StickerResponse>, response: Response<StickerResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    val stickers = response.body()!!.item!!.stickers
                                    if (stickers.isNotEmpty()) {
                                        for (s in stickers) {
                                            updateGainedSticker(s.sticker_no)
                                        }
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

class StickerViewModelFactory(private val serverRepo: StickerServerRepository,
                              private val dbRepo: StickerRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StickerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StickerViewModel(serverRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}