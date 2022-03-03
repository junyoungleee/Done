package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.DoneApplication
import com.palette.done.data.remote.model.member.*
import com.palette.done.repository.MemberRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(private val repository: MemberRepository) : ViewModel(){

    var isResponse: MutableLiveData<Boolean> = MutableLiveData(false)
    var isLoginSuccess: MutableLiveData<Boolean> = MutableLiveData(true)
    var isSignUpSuccess: MutableLiveData<Boolean> = MutableLiveData(true)

    var isNew : MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    var _email: MutableLiveData<String> = MutableLiveData("")

    fun postEmailCheck(email: String) {
        viewModelScope.launch {
            repository.postEmailCheck(CheckEmail(email)).enqueue(object : Callback<CheckEmailResponse> {
                override fun onResponse(call: Call<CheckEmailResponse>, response: Response<CheckEmailResponse>) {
                    isNew.value = response.body()!!.is_success
                    isResponse.value = true
                    _email.value = email
                    Log.d("loginVM_new", "${response.body()!!.is_success}")
                }

                override fun onFailure(call: Call<CheckEmailResponse>, t: Throwable) {
                    isResponse.value = false
                    Log.d("retrofit_failure", "${t.message}")
                }
            })
        }
    }

    fun postLogin(pwd: String) {
        viewModelScope.launch {
            repository.postMemberLogin(MemberAccount(_email.value.toString(), pwd)).enqueue(object : Callback<MemberLoginResponse> {
                override fun onResponse(call: Call<MemberLoginResponse>, response: Response<MemberLoginResponse>) {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                isLoginSuccess.value = true
                                DoneApplication.pref.email = _email.value.toString()
                                DoneApplication.pref.pwd = pwd
                                DoneApplication.pref.token = response.body()!!.item?.access_token
                                Log.d("retrofit_200_token", "${response.body()!!.item?.access_token}")
                            }
                            400 -> {
                                isLoginSuccess.value = false
                                Log.d("retrofit_400", "${response.body()!!.message}")
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<MemberLoginResponse>, t: Throwable) {
                    Log.d("retrofit_failure", "${t.message}")
                }
            })
        }
    }

    fun postSignUp(pwd: String) {
        viewModelScope.launch {
            repository.postMemberSignUp(MemberAccount(_email.value.toString(), pwd)).enqueue(object : Callback<MemberSignUpResponse> {
                override fun onResponse(call: Call<MemberSignUpResponse>, response: Response<MemberSignUpResponse>) {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                isSignUpSuccess.value = true
                                postLogin(pwd)
                            }
                            400 -> {
                                isSignUpSuccess.value = false
                                Log.d("retrofit_400", "${response.body()!!.message}")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<MemberSignUpResponse>, t: Throwable) {
                    Log.d("retrofit_failure", "${t.message}")
                }
            })
        }
    }
}

class LoginViewModelFactory(private val repository: MemberRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}