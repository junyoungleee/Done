package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.datasource.StickerRepository
import com.palette.done.data.remote.model.member.MemberDeleteResponse
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.data.remote.repository.StickerServerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OutViewModel(private val serverRepo: MemberRepository,
                   private val doneRepo: DoneRepository,
                   private val stickerRepo: StickerRepository): ViewModel() {

    var _isResponse: MutableLiveData<Boolean> = MutableLiveData(false)
    val isResponse: MutableLiveData<Boolean> get() = _isResponse

    fun logOut() {
        CoroutineScope(Dispatchers.IO).launch {
            async {
                doneRepo.deleteAllDone()
                doneRepo.deleteAllTodayRecord()
                doneRepo.deleteAllPlan()
                doneRepo.deleteAllRoutine()
                stickerRepo.resetAllGainedHistory()
            }.await()
        }
        _isResponse.value = true
    }

    fun deleteMemberInServer() {
        viewModelScope.launch {
            serverRepo.deleteMember()
                .enqueue(object : Callback<MemberDeleteResponse> {
                    override fun onResponse(call: Call<MemberDeleteResponse>, response: Response<MemberDeleteResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    logOut()
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<MemberDeleteResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

}

class OutViewModelFactory(private val serverRepo: MemberRepository,
                          private val doneRepo: DoneRepository,
                          private val stickerRepo: StickerRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OutViewModel(serverRepo, doneRepo, stickerRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}