package com.palette.done.viewmodel

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DoneEditViewModel : ViewModel() {

    var _done: MutableLiveData<String> = MutableLiveData("")
    val done: LiveData<String> get() = _done

    fun onDoneTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                _done.value = s.toString()
            }
        }
    }
}