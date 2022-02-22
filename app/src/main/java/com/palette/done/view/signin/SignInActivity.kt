package com.palette.done.view.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.palette.done.R
import com.palette.done.databinding.ActivitySignInBinding
import com.palette.done.view.login.FindPwdActivity
import com.palette.done.viewmodel.PatternCheckViewModel

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    private val patternVM : PatternCheckViewModel by viewModels()
    private var agree: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonsDestination()
        setAgreeButton()
        checkSigninAvailable()
    }

    private fun setButtonsDestination() {
        lateinit var intent: Intent
        // 온보딩으로 이동
        binding.btnSignIn.setOnClickListener {
            intent = Intent(this, OnBoardingActivity::class.java)
            startActivity(intent)
        }
        // back 버튼
        binding.btnBack.setOnClickListener {
            finish()
        }
    }


    // 동의 체크
    private fun setAgreeButton() {
        binding.ivAllAgree.setOnClickListener {
            if (!agree) {
                binding.ivAllAgree.isSelected = true
                binding.ivFirstAgree.isSelected = true
                binding.ivSecondAgree.isSelected = true
                agree = true
            } else {
                binding.ivAllAgree.isSelected = false
                binding.ivFirstAgree.isSelected = false
                binding.ivSecondAgree.isSelected = false
                agree = false
            }
            setLoginButtonEnable()
        }
    }

    private fun checkEmail(check: Boolean) {
        if (!check) {
            binding.etEmail.setBackgroundResource(R.drawable.all_et_background_error)
            binding.etEmail.setTextColor(ContextCompat.getColor(applicationContext, R.color.errorColor))
            binding.tvWrongSign.setText(R.string.login_wrong_email)
        } else {
            binding.etEmail.setBackgroundResource(R.drawable.all_et_background_selector)
            binding.etEmail.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            binding.tvWrongSign.text = ""
        }
    }

    private fun checkPwd(check: Boolean) {
        if (!check) {
            binding.etPwd.setBackgroundResource(R.drawable.all_et_background_error)
            binding.etPwd.setTextColor(ContextCompat.getColor(applicationContext, R.color.errorColor))
            binding.tvWrongSign.setText(R.string.login_wrong_pwd)
        } else {
            binding.etPwd.setBackgroundResource(R.drawable.all_et_background_selector)
            binding.etPwd.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            binding.tvWrongSign.text = ""
        }
    }

    private fun setLoginButtonEnable() {
        binding.btnSignIn.isEnabled = (patternVM.emailResult.value == true && patternVM.pwdResult.value == true) &&
                (binding.etPwd.text.toString() != "" && binding.etEmail.text.toString() != "") && agree
    }

    private fun checkSigninAvailable() {
        binding.etEmail.addTextChangedListener(patternVM.onEmailTextWatcher())
        binding.etPwd.addTextChangedListener(patternVM.onPwdTextWatcher())

        patternVM.emailResult.observe(this) {
            checkEmail(it)
            setLoginButtonEnable()
        }
        patternVM.pwdResult.observe(this) {
            checkPwd(it)
            setLoginButtonEnable()
        }
    }
}