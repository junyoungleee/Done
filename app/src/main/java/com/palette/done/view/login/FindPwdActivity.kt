package com.palette.done.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.R
import com.palette.done.databinding.ActivityFindPwdBinding
import com.palette.done.view.signin.SignInActivity

class FindPwdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindPwdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPwdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonsDestination()
    }

    private fun setButtonsDestination() {
        lateinit var intent: Intent
        // 이메일 전송 버튼
        binding.btnSendEmail.setOnClickListener {
            intent = Intent(this, AfterFindPwdActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }

        // 툴바 백버튼
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}