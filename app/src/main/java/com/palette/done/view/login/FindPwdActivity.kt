package com.palette.done.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.databinding.ActivityFindPwdBinding
import com.palette.done.viewmodel.LoginViewModel
import com.palette.done.viewmodel.LoginViewModelFactory
import com.palette.done.viewmodel.PatternCheckViewModel

class FindPwdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindPwdBinding

    private val patternVM : PatternCheckViewModel by viewModels()
    private val loginVM : LoginViewModel by viewModels { LoginViewModelFactory(
        MemberRepository(), DoneServerRepository(), DoneApplication().doneRepository
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPwdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonsDestination()
        setNextButtonEnable()
    }

    private fun setButtonsDestination() {
        lateinit var intent: Intent
        // 이메일 전송 버튼
        binding.btnSendEmail.setOnClickListener {
            loginVM.postEmailPwd(binding.etEmail.text.toString())
            loginVM.isPwdEmailSent.observe(this) { sent ->
                if(sent) {
                    intent = Intent(this, AfterFindPwdActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }

        // 툴바 백버튼
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun checkEmail(check: Boolean) {
        if (!check) {
            binding.etEmail.setBackgroundResource(R.drawable.all_et_background_error)
            binding.etEmail.setTextColor(ContextCompat.getColor(this, R.color.errorColor))
            binding.tvWrongSign.setText(R.string.login_wrong_email)
        } else {
            binding.etEmail.setBackgroundResource(R.drawable.all_et_background_selector)
            binding.etEmail.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.tvWrongSign.text = ""
        }
    }

    private fun setNextButtonEnable() {
        binding.etEmail.addTextChangedListener(patternVM.onEmailTextWatcher())
        patternVM.emailResult.observe(this) {
            checkEmail(it)
            binding.btnSendEmail.isEnabled = (patternVM.emailResult.value == true) && (binding.etEmail.text.toString() != "")
        }
    }
}