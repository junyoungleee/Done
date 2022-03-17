package com.palette.done.view.util

import android.util.Log
import java.util.regex.Pattern

class InputForm {

    fun checkEmailPattern(email: String): Boolean {
        // 이메일 정규식
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun checkPwdPattern(pwd: String): Boolean {
        // 비밀번호 정규식 : 영문, 숫자 포함 8~16자
        val pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9]).{8,16}.\$")
        return pattern.matcher(pwd).matches()
    }
}