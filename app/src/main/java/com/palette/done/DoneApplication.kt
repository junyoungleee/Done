package com.palette.done

import android.app.Application
import com.palette.done.data.PreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DoneApplication : Application() {
    companion object {
        lateinit var pref: PreferenceManager
    }

    override fun onCreate() {
        super.onCreate()
        pref = PreferenceManager(applicationContext)

    }
}