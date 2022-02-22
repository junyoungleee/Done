package com.palette.done.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.databinding.ActivityLoginBinding
import com.palette.done.view.adapter.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewPager()
        setButtonsDestination()
    }

    private fun setButtonsDestination() {
        lateinit var intent: Intent
        // 비밀번호 찾기로 이동
        // 툴바 백버튼
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setViewPager() {
        val fragments = arrayListOf(LoginEmailFragment(), LoginPwdFragment())
        val adapter = ViewPagerAdapter(this, fragments)
        binding.viewPagerLogin.adapter = adapter
        binding.viewPagerLogin.isUserInputEnabled = false
    }
}


