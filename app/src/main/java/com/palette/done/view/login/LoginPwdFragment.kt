package com.palette.done.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.palette.done.R
import com.palette.done.databinding.FragmentLoginPwdBinding
import com.palette.done.databinding.FragmentObAlarmBinding
import com.palette.done.view.MainActivity
import com.palette.done.view.StartActivity
import com.palette.done.view.signin.OnBoardingActivity
import com.palette.done.viewmodel.PatternCheckViewModel

class LoginPwdFragment : Fragment() {

    private var _binding: FragmentLoginPwdBinding? = null
    private val binding get() = _binding!!

    private val patternVM: PatternCheckViewModel by viewModels()
    private var isNew: Boolean = false // 신규 or 기존 유저

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginPwdBinding.inflate(inflater, container, false)

        isNewUser(isNew)
        setButtonsDestination()
        checkBothPwd(isNew)

        return binding.root
    }

    private fun setButtonsDestination() {
        lateinit var intent: Intent
        // 비밀번호 찾기로 이동
        binding.tvFindPwd.setOnClickListener {
            intent = Intent(requireContext(), FindPwdActivity::class.java)
            startActivity(intent)
        }
        binding.btnNext.setOnClickListener {
            intent = if (isNew) {
                // 신규 회원이라면 온보딩
                Intent(requireContext(), OnBoardingActivity::class.java)
            } else {
                // 기존 회원이라면 메인화면
                Intent(requireContext(), MainActivity::class.java)
            }
            // 이전 액티비티 스택 모두 제거
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // 자동 로그인 상태로 만들어야 함

            startActivity(intent)
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
            setNextButtonEnable(isNew)
        }
        // 신규유저인 경우 재입력까지 확인해야 함
        patternVM.pwd2Result.observe(viewLifecycleOwner) { result ->
            checkPwdAgain(result)
            if (patternVM.pwdResult.value == false && result == false) {
                // 비밀번호 형식이 잘못된 경우, 해당 사인 우선 표시
                binding.tvWrongSign.setText(R.string.login_wrong_pwd)
            }
            setNextButtonEnable(isNew)
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

    private fun isNewUser(new: Boolean) {
        if (new) {
            // 신규 회원 : 중복X
            binding.etPwdAgain.visibility = View.VISIBLE
            binding.tvFindPwd.visibility = View.GONE
        } else {
            // 기존 회원 : 중복
            binding.etPwdAgain.visibility = View.GONE
            binding.tvFindPwd.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}