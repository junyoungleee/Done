package com.palette.done.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.palette.done.view.login.InputForm

class PatternCheckViewModel @ViewModelInject constructor(): ViewModel(){

    var _emailResult: MutableLiveData<Boolean> = MutableLiveData(true)
    var _pwdResult: MutableLiveData<Boolean> = MutableLiveData(true)
    val emailResult: LiveData<Boolean> get() = _emailResult
    val pwdResult: LiveData<Boolean> get() = _pwdResult


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
            }
            override fun afterTextChanged(s: Editable?) {
                _pwdResult.value = inputForm.checkPwdPattern(s.toString())
            }
        }
    }



}