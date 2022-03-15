package com.palette.done.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.databinding.ActivityStartBinding
import com.palette.done.view.login.LoginActivity

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLoginButtonsListener()
    }

    // 로그인 버튼 클릭 리스너 메서드
    private fun setLoginButtonsListener() {
        binding.btnLogin.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnKakaoLogin.setOnClickListener {

        }
    }
}