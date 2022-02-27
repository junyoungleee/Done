package com.palette.done.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

class PreferenceManager (context: Context) {
    private val FIRST = "first"
    private val SIGN_UP = "signup"
    private val USER_EMAIL = "email"
    private val USER_PWD = "pwd"
    private val ACCESS_TOKEN = "token"

    private val PREF_NAME = "user_info"
    private val PREF_MODE = Context.MODE_PRIVATE
    var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, PREF_MODE)

    var first: Boolean
        get() = prefs.getBoolean(FIRST, true)
        set(value) = prefs.edit().putBoolean(FIRST, value).apply()

    var signup: String?
        get() = prefs.getString(SIGN_UP, "")
        set(value) = prefs.edit().putString(SIGN_UP, value).apply()

    var email: String?
        get() = prefs.getString(USER_EMAIL, "")
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    var pwd: String?
        get() = prefs.getString(USER_PWD, "")
        set(value) = prefs.edit().putString(USER_PWD, value).apply()

    var token: String?
        get() = prefs.getString(ACCESS_TOKEN, "")
        set(value) = prefs.edit().putString(ACCESS_TOKEN, value).apply()
}