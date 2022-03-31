package com.palette.done.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.DoneApplication
import com.palette.done.databinding.ActivityStartBinding
import com.palette.done.view.login.LoginActivity

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLoginButtonsListener()
        setNoticeLink()

        initPref()
    }

    // 로그인 버튼 클릭 리스너 메서드
    private fun setLoginButtonsListener() {
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnKakaoLogin.setOnClickListener {

        }
    }

    private fun setNoticeLink() {
        with(binding) {
            tvTermOfUse.setOnClickListener {
                val intentUrl = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://spark-myth-3c9.notion.site/f7c405f9a9a644a8964ad6046530d08f"))
                startActivity(intentUrl)
            }
            tvPrivacy.setOnClickListener {
                val intentUrl = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://spark-myth-3c9.notion.site/95812922d5cc4f9aa47f32936f5e9919"))
                startActivity(intentUrl)
            }
        }
    }

    private fun initPref() {
        val pref = DoneApplication.pref
        val todayFirst = pref.todayFirst

        val prefData = getSharedPreferences(pref.PREF_NAME, pref.PREF_MODE)
        val editor = prefData.edit()
        editor.clear()
        editor.commit()

        pref.todayFirst = todayFirst  // 로그아웃 했다가 다시 돌아올 때
        pref.first = false  // 첫방문 아님
    }
}