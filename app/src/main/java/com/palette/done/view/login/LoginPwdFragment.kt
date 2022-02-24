package com.palette.done.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.FragmentLoginPwdBinding
import com.palette.done.repository.MemberRepository
import com.palette.done.view.main.MainActivity
import com.palette.done.view.signin.OnBoardingActivity
import com.palette.done.viewmodel.LoginViewModel
import com.palette.done.viewmodel.LoginViewModelFactory
import com.palette.done.viewmodel.PatternCheckViewModel
import dagger.hilt.android.AndroidEntryPoint


class LoginPwdFragment : Fragment() {

    private var _binding: FragmentLoginPwdBinding? = null
    private val binding get() = _binding!!

    private val loginVM : LoginViewModel by activityViewModels { LoginViewModelFactory(MemberRepository()) }
    private val patternVM: PatternCheckViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginPwdBinding.inflate(inflater, container, false)

        loginVM.isNew.observe(viewLifecycleOwner) {
            Log.d("loginVM_pwd1", "${loginVM.isNew.value}")
            setNewUserUI(loginVM.isNew.value!!)
            setButtonsDestination(loginVM.isNew.value!!)
            checkBothPwd(loginVM.isNew.value!!)
        }
        Log.d("loginVM_pwd2", "${loginVM.isNew.value}")

        return binding.root
    }

    private fun setButtonsDestination(new: Boolean) {
        lateinit var intent: Intent
        // 비밀번호 찾기로 이동
        binding.tvFindPwd.setOnClickListener {
            intent = Intent(requireContext(), FindPwdActivity::class.java)
            startActivity(intent)
        }
        binding.btnNext.setOnClickListener {
            if (new) {
                // 신규 회원이라면 온보딩
                intent = Intent(requireContext(), OnBoardingActivity::class.java)
//                loginVM.postSignUp(binding.etPwd.text.toString())

                // 이전 액티비티 스택 모두 제거
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

//                loginVM.isSignUpSuccess.observe(viewLifecycleOwner) {
//                    if (loginVM.isSignUpSuccess.value!!) {
//                        startActivity(intent)
//                    }
//                }
                startActivity(intent)
            } else {
                // 기존 회원이라면 메인화면
                intent = Intent(requireContext(), MainActivity::class.java)
//                loginVM.postLogin(binding.etPwd.text.toString())

                // 이전 액티비티 스택 모두 제거
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

//                loginVM.isLoginSuccess.observe(viewLifecycleOwner) {
//                    if (loginVM.isLoginSuccess.value!!) {
//                        startActivity(intent)
//                    }
//                }
                startActivity(intent)
            }
        }
    }

    private fun checkPwd(check: Boolean) {
        if (!check) {
            binding.etPwd.setBackgroundResource(R.drawable.all_et_background_error)
            binding.etPwd.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor))
            binding.tvWrongSign.setText(R.string.login_wrong_pwd)
        } else {
            binding.etPwd.setBackgroundResource(R.drawable.all_et_background_selector)
            binding.etPwd.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvWrongSign.text = ""
        }
    }

    private fun checkPwdAgain(check: Boolean) {
        if (!check) {
            binding.etPwdAgain.setBackgroundResource(R.drawable.all_et_background_error)
            binding.etPwdAgain.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor))
            binding.tvWrongSign.setText(R.string.login_diff_pwd)
        } else {
            binding.etPwdAgain.setBackgroundResource(R.drawable.all_et_background_selector)
            binding.etPwdAgain.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvWrongSign.text = ""
        }
    }

    private fun checkBothPwd(new: Boolean) {
        binding.etPwd.addTextChangedListener(patternVM.onPwdTextWatcher())
        binding.etPwdAgain.addTextChangedListener(patternVM.onPwd2TextWatcher())

        // 기존유저인 경우 비밀번호만 확인함
        patternVM.pwdResult.observe(viewLifecycleOwner) {
            checkPwd(it)
            setNextButtonEnable(new)
        }
        // 신규유저인 경우 재입력까지 확인해야 함
        patternVM.pwd2Result.observe(viewLifecycleOwner) { result ->
            checkPwdAgain(result)
            if (patternVM.pwdResult.value == false && result == false) {
                // 비밀번호 형식이 잘못된 경우, 해당 사인 우선 표시
                binding.tvWrongSign.setText(R.string.login_wrong_pwd)
            }
            setNextButtonEnable(new)
        }
    }

    private fun setNextButtonEnable(new: Boolean) {
        if (new) {
            binding.btnNext.isEnabled =
                (patternVM.pwdResult.value == true) && (patternVM.pwd2Result.value == true) &&
                        (binding.etPwd.text.toString() != "") && (binding.etPwdAgain.text.toString() != "")
        } else {
            binding.btnNext.isEnabled =
                (patternVM.pwdResult.value == true) && (binding.etPwd.text.toString() != "")
        }
    }

    private fun setNewUserUI(new: Boolean) {
        if (new) {
            // 신규 회원 : 중복X
            activity?.findViewById<TextView>(R.id.tv_login_pwd_title)!!.text = resources.getString(R.string.login_title_sign_up)
            binding.tvTitle.text = getString(R.string.login_title_pwd_new)
            binding.etPwdAgain.visibility = View.VISIBLE
            binding.tvFindPwd.visibility = View.GONE
        } else {
            // 기존 회원 : 중복
            activity?.findViewById<TextView>(R.id.tv_login_pwd_title)!!.text = resources.getString(R.string.login_title_login)
            binding.tvTitle.text = getString(R.string.login_title_pwd_old)
            binding.etPwdAgain.visibility = View.GONE
            binding.tvFindPwd.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}