package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.DoneApplication
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.entity.Category
import com.palette.done.data.db.entity.Member
import com.palette.done.data.remote.model.dones.CategoryResponse
import com.palette.done.data.remote.model.member.*
import com.palette.done.data.remote.repository.MemberRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyEditViewModel(private val serverRepo: MemberRepository
) : ViewModel() {

    var _nickResult: MutableLiveData<Boolean> = MutableLiveData(false)
    val nickResult: LiveData<Boolean> get() = _nickResult

    var type: String = ""

    var _typeResult: MutableLiveData<Boolean> = MutableLiveData(false)
    val typeResult: LiveData<Boolean> get() = _typeResult

    var _isResponse: MutableLiveData<Boolean> = MutableLiveData(false)
    val isResponse: MutableLiveData<Boolean> get() = _isResponse

    fun checkNickname(nickname: String) {
        val old = DoneApplication.pref.nickname
        _nickResult.value = (old != nickname) && (nickname != "")
    }

    fun checkType(type: String) {
        val old = DoneApplication.pref.type
        this.type = type
        _typeResult.value = old != type
    }

    fun patchNickname(nickname: String) {
        viewModelScope.launch {
            serverRepo.patchNewNickName(MemberNickname(nickname))
                .enqueue(object : Callback<MemberProfileResponse> {
                    override fun onResponse(call: Call<MemberProfileResponse>, response: Response<MemberProfileResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    DoneApplication.pref.nickname = nickname
                                    _isResponse.value = true
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<MemberProfileResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun patchType() {
        viewModelScope.launch {
            serverRepo.patchNewType()
                .enqueue(object : Callback<MemberTypeResponse> {
                    override fun onResponse(call: Call<MemberTypeResponse>, response: Response<MemberTypeResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    DoneApplication.pref.type = type
                                    _isResponse.value = true
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<MemberTypeResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun patchPassword(pwd: String) {
        viewModelScope.launch {
            serverRepo.patchNewPwd(MemberPwd(pwd))
                .enqueue(object : Callback<MemberChangePwdResponse> {
                    override fun onResponse(call: Call<MemberChangePwdResponse>, response: Response<MemberChangePwdResponse>) {
                        if (response.isSuccessful) {
                            when (response.code()) {
                                200 -> {
                                    DoneApplication.pref.pwd = pwd
                                    _isResponse.value = true
                                }
                                400 -> {
                                    Log.d("retrofit_400", "${response.body()!!.message}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<MemberChangePwdResponse>, t: Throwable) {
                        Log.d("retrofit_failure", "${t.message}")
                    }
                })
        }
    }

    fun initResponseValue() {
        _isResponse.value = false
    }

}
class MyEditViewModelFactory(private val serverRepo: MemberRepository,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyEditViewModel(serverRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}