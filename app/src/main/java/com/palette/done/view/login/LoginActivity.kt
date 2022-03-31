package com.palette.done.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.palette.done.DoneApplication
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.databinding.ActivityLoginBinding
import com.palette.done.view.StartActivity
import com.palette.done.view.adapter.ViewPagerAdapter
import com.palette.done.viewmodel.LoginViewModel
import com.palette.done.viewmodel.LoginViewModelFactory
import dagger.hilt.android.AndroidEntryPoint

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
        // 툴바 백버튼
        binding.btnBack.setOnClickListener {
            var current = binding.viewPagerLogin.currentItem
            if (current == 1) {
                binding.viewPagerLogin.currentItem = 0
            } else {
                intent = Intent(this, StartActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
    }

    private fun setViewPager() {
        val fragments = arrayListOf(LoginEmailFragment(), LoginPwdFragment())
        val adapter = ViewPagerAdapter(this, fragments)
        binding.viewPagerLogin.adapter = adapter
        binding.viewPagerLogin.isUserInputEnabled = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // 시스템 back 버튼 클릭 시, 시작 화면으로 이동
        var intent = Intent(this, StartActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}


