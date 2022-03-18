package com.palette.done.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.DoneApplication
import com.palette.done.databinding.ActivitySplashBinding
import com.palette.done.view.main.MainActivity
import com.palette.done.view.signin.OnBoardingActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lateinit var intent: Intent

        // 자동 로그인
        if (DoneApplication.pref.first) {
            // 앱 설치 후 처음 방문
            DoneApplication.pref.first = false  // 재방문
            intent = Intent(this, LandingActivity::class.java)
        } else {
            when(DoneApplication.pref.signup) {
                "true" -> {
                    // 자동 로그인
                    intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                "false" -> {
                    // 로그인은 되었고, 유저 정보가 없는 상태
                    intent = Intent(this, OnBoardingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                }
                "" -> {
                    // 로그아웃 or 재방문한 신규
                    intent = Intent(this, StartActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            // delay(3000L)
            delay(3000L)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}