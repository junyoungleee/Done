package com.palette.done.viewmodel

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.palette.done.view.util.InputForm

class PatternCheckViewModel: ViewModel(){

    var _emailResult: MutableLiveData<Boolean> = MutableLiveData(true)
    var _pwd: MutableLiveData<String> = MutableLiveData("")
    var _pwd2: MutableLiveData<String> = MutableLiveData("")
    var _pwdResult: MutableLiveData<Boolean> = MutableLiveData(true)
    var _pwd2Result: MutableLiveData<Boolean> = MutableLiveData(true)

    val emailResult: LiveData<Boolean> get() = _emailResult
    val pwd: LiveData<String> get() = _pwd
    val pwd2: LiveData<String> get() = _pwd2
    val pwdResult: LiveData<Boolean> get() = _pwdResult
    val pwd2Result: LiveData<Boolean> get() = _pwd2Result

    val inputForm = InputForm()

    fun onEmailTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _emailResult.value = inputForm.checkEmailPattern(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {
                _emailResult.value = inputForm.checkEmailPattern(s.toString())
            }
        }
    }

    fun onPwdTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _pwdResult.value = inputForm.checkPwdPattern(s.toString())
                _pwd.value = s.toString()
                // 재입력이 이미 입력되어 있는 상태에서 비밀번호가 바뀐 경우
                if (!_pwd2.value.equals("")) {
                    _pwd2Result.value = (pwd2.value.toString() == (s.toString()))
                }
            }
            override fun afterTextChanged(s: Editable?) {
                _pwdResult.value = inputForm.checkPwdPattern(s.toString())
                _pwd.value = s.toString()
                if (!_pwd2.value.equals("")) {
                    _pwd2Result.value = (pwd2.value.toString() == (s.toString()))
                }
            }
        }
    }

    fun onPwd2TextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                _pwd2Result.value = (pwd.value.toString() == (s.toString()))
                _pwd2.value = s.toString()
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _pwd2Result.value = (pwd.value.toString() == (s.toString()))
                _pwd2.value = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
                _pwd2Result.value = (pwd.value.toString() == (s.toString()))
                _pwd2.value = s.toString()
            }
        }
    }

}