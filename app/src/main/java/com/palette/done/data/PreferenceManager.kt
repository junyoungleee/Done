package com.palette.done.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

class PreferenceManager (context: Context) {
    private val FIRST = "first"
    private val TODAY_FIRST = "today_first"
    private val SIGN_UP = "signup"

    private val USER_EMAIL = "email"
    private val USER_PWD = "pwd"

    private val USER_NICKNAME = "nickname"
    private val USER_TYPE = "type"
    private val USER_ALARM_CYCLE = "alarm_cycle"
    private val USER_ALARM_TIME = "alarm_time"
    private val LEVEL = "level"
    private val PREMIUM = "premium"

    private val ACCESS_TOKEN = "token"
    private val KEYBOARD_HEIGHT = "keyboard_height"

    val PREF_NAME = "user_info"
    val PREF_MODE = Context.MODE_PRIVATE
    var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, PREF_MODE)

    var first: Boolean
        get() = prefs.getBoolean(FIRST, true)
        set(value) = prefs.edit().putBoolean(FIRST, value).apply()

    var todayFirst: String?
        get() = prefs.getString(TODAY_FIRST, "")
        set(value) = prefs.edit().putString(TODAY_FIRST, value).apply()

    var signup: String?
        get() = prefs.getString(SIGN_UP, "")
        set(value) = prefs.edit().putString(SIGN_UP, value).apply()

    var email: String?
        get() = prefs.getString(USER_EMAIL, "")
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    var pwd: String?
        get() = prefs.getString(USER_PWD, "")
        set(value) = prefs.edit().putString(USER_PWD, value).apply()

    // 사용자 정보 -----------------------------------------------------------------------------------
    var nickname: String?
        get() = prefs.getString(USER_NICKNAME, "")
        set(value) = prefs.edit().putString(USER_NICKNAME, value).apply()

    var type: String?
        get() = prefs.getString(USER_TYPE, "")
        set(value) = prefs.edit().putString(USER_TYPE, value).apply()

    var alarmCycle: String?
        get() = prefs.getString(USER_ALARM_CYCLE, "")
        set(value) = prefs.edit().putString(USER_ALARM_CYCLE, value).apply()

    var alarmTime: String?
        get() = prefs.getString(USER_ALARM_TIME, "")
        set(value) = prefs.edit().putString(USER_ALARM_TIME, value).apply()

    var level: Int
        get() = prefs.getInt(LEVEL, 1)
        set(value) = prefs.edit().putInt(LEVEL, value).apply()

    var premium: Boolean
        get() = prefs.getBoolean(PREMIUM, false)
        set(value) = prefs.edit().putBoolean(PREMIUM, value).apply()
    // ---------------------------------------------------------------------------------------------
    var token: String?
        get() = prefs.getString(ACCESS_TOKEN, "")
        set(value) = prefs.edit().putString(ACCESS_TOKEN, value).apply()

    var keyboard: Int
        get() = prefs.getInt(KEYBOARD_HEIGHT, -1)
        set(value) = prefs.edit().putInt(KEYBOARD_HEIGHT, value).apply()
}