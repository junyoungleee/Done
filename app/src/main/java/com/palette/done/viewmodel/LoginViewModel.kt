package com.palette.done.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.palette.done.DoneApplication
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.entity.TodayRecord
import com.palette.done.data.remote.model.dones.DonesAndTodayResponse
import com.palette.done.data.remote.model.member.*
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.data.remote.repository.MemberRepository
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(private val repository: MemberRepository,
                    private val dataRepo: DoneServerRepository,
                    private val dbRepo: DoneRepository) : ViewModel(){

    var isResponse: MutableLiveData<Boolean> = MutableLiveData(false)
    var isLoginSuccess: MutableLiveData<Boolean> = MutableLiveData(true)
    var isSignUpSuccess: MutableLiveData<Boolean> = MutableLiveData(true)
    var isDataSaved: MutableLiveData<Boolean> = MutableLiveData(false)

    var isNew : MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    var _email: MutableLiveData<String> = MutableLiveData("")

    var _isPwdEmailSent: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPwdEmailSent: LiveData<Boolean> get() = _isPwdEmailSent

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
                                DoneApplication.pref.email = _email.value.toString()
                                DoneApplication.pref.pwd = pwd
                                DoneApplication.pref.token = response.body()!!.item?.access_token
                                isLoginSuccess.value = true
                                getAllData()
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

    private fun getAllData() {
        viewModelScope.launch {
            dataRepo.getDoneAndTodayRecord(null).enqueue(object : Callback<DonesAndTodayResponse> {
                override fun onResponse(call: Call<DonesAndTodayResponse>, response: Response<DonesAndTodayResponse>) {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                val dataList = ArrayList(response.body()!!.item!!.all_dones)
                                viewModelScope.launch {
                                    async {
                                        getUserType()
                                        getUserInfo()
                                        if (dataList.isNotEmpty()) {
                                            for (data in dataList) {
                                                val date = data.date
                                                val dones = data.dones
                                                val tr = data.today_record
                                                for (d in dones) {
                                                    val done = Done(d.done_no, date, d.content, d.category_no, d.tag_no, d.routine_no)
                                                    insertDoneInDB(done)
                                                }
                                                if (tr != null) {
                                                    val todayRecord = TodayRecord(tr.today_no, date, tr.content, tr.sticker_no)
                                                    insertTodayRecordInDB(todayRecord)
                                                }
                                            }
                                        } else {
                                            isDataSaved.value = true
                                        }
                                    }.await()
                                    isDataSaved.value = true
                                    Log.d("로그인_vm", "true")
                                }
                            }
                            400 -> {
                                isDataSaved.value = false
                                Log.d("retrofit_400", "${response.body()!!.message}")
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<DonesAndTodayResponse>, t: Throwable) {
                    Log.d("retrofit_failure", "${t.message}")
                }
            })
        }
    }

    private fun getUserType() {
        viewModelScope.launch {
            repository.getMemberType().enqueue(object : Callback<MemberTypeResponse> {
                override fun onResponse(call: Call<MemberTypeResponse>, response: Response<MemberTypeResponse>) {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                DoneApplication.pref.type = response.body()!!.item!!.type
                            }
                            400 -> {
                                isDataSaved.value = false
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

    private fun getUserInfo() {
        viewModelScope.launch {
            repository.getMemberInfo().enqueue(object : Callback<MemberInfoResponse> {
                override fun onResponse(call: Call<MemberInfoResponse>, response: Response<MemberInfoResponse>) {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                val data = response.body()!!.item
                                if(data != null) {
                                    DoneApplication.pref.nickname = data!!.nickname
                                    DoneApplication.pref.level = data!!.level
                                }
                            }
                            400 -> {
                                isDataSaved.value = false
                                Log.d("retrofit_400", "${response.body()!!.message}")
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {
                    Log.d("retrofit_failure", "${t.message}")
                }
            })
        }
    }

    private fun insertDoneInDB(done: Done) {
        viewModelScope.launch {
            dbRepo.insertDone(done)
        }
    }

    private fun insertTodayRecordInDB(todayRecord: TodayRecord) {
        viewModelScope.launch {
            dbRepo.insertTodayRecord(todayRecord)
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

    fun postEmailPwd(email: String) {
        viewModelScope.launch {
            repository.postEmailPwd(CheckEmail(email)).enqueue(object : Callback<CheckEmailResponse> {
                override fun onResponse(call: Call<CheckEmailResponse>, response: Response<CheckEmailResponse>) {
                    _isPwdEmailSent.value = true
                }

                override fun onFailure(call: Call<CheckEmailResponse>, t: Throwable) {
                    isResponse.value = false
                    Log.d("retrofit_failure", "${t.message}")
                }
            })
        }
    }


}

class LoginViewModelFactory(private val repository: MemberRepository,
                            private val dataRepo: DoneServerRepository,
                            private val dbRepo: DoneRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, dataRepo, dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}