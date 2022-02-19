package com.palette.done.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.palette.done.R
import com.palette.done.databinding.ActivityLoginBinding
import com.palette.done.view.signin.SignInActivity
import com.palette.done.viewmodel.PatternCheckViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val patternVM : PatternCheckViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoginAvailable()
        setButtonsDestination()
    }

    private fun setButtonsDestination() {
        lateinit var intent: Intent
        binding.tvFindPwd.setOnClickListener {
            intent = Intent(this, FindPwdActivity::class.java)
            startActivity(intent)
        }
        binding.tvSignIn.setOnClickListener {
            intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setLoginButtonEnable() {
        binding.btnLogin.isEnabled = (patternVM.emailResult.value == true && patternVM.pwdResult.value == true) &&
                (binding.etPwd.text.toString() != "" && binding.etEmail.text.toString() != "")
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

    private fun checkLoginAvailable() {
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


