package com.palette.done.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.FragmentLoginPwdBinding
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.view.main.MainActivity
import com.palette.done.view.my.MyEditActivity
import com.palette.done.view.signin.OnBoardingActivity
import com.palette.done.viewmodel.*

class LoginPwdFragment(edit: Boolean = false) : Fragment() {

    private var _binding: FragmentLoginPwdBinding? = null
    private val binding get() = _binding!!

    private val editMode: Boolean = edit

    private val myEditVM: MyEditViewModel by activityViewModels { MyEditViewModelFactory(
        MemberRepository()
    ) }
    private val loginVM : LoginViewModel by activityViewModels { LoginViewModelFactory(
        MemberRepository(), DoneServerRepository(), (requireActivity().application as DoneApplication).doneRepository
    ) }

    private val patternVM: PatternCheckViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginPwdBinding.inflate(inflater, container, false)

        if (!editMode) {
            loginVM.isNew.observe(viewLifecycleOwner) {
                Log.d("loginVM_pwd1", "${loginVM.isNew.value}")
                setNewUserUI(loginVM.isNew.value!!)
                setButtonsDestination(loginVM.isNew.value!!)
                checkBothPwd(loginVM.isNew.value!!)
            }
        } else {
            initEditPwdFragment()
            checkEditPwd()
            setEditButton()
        }

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
                loginVM.postSignUp(binding.etPwd.text.toString())
                DoneApplication.pref.signup = "false"

                // 이전 액티비티 스택 모두 제거
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                loginVM.isSignUpSuccess.observe(viewLifecycleOwner) {
                    if (loginVM.isSignUpSuccess.value!!) {
                        startActivity(intent)
                    }
                }
                startActivity(intent)
            } else {
                // 기존 회원이라면 메인화면
                intent = Intent(requireContext(), MainActivity::class.java)
                loginVM.postLogin(binding.etPwd.text.toString())

                // 이전 액티비티 스택 모두 제거
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                // 로그인 성공과 데이터 response 성공 분리하기
                loginVM.isLoginSuccess.observe(viewLifecycleOwner) {
                    if (!it) {
                        checkLogin(false)
                    }
                }
                loginVM.isDataSaved.observe(viewLifecycleOwner) {
                    if (it) {
                        DoneApplication.pref.signup = "true"
                        startActivity(intent)
                    }
                }
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

    private fun checkLogin(check: Boolean) {
        if (!check) {
            binding.etPwd.setBackgroundResource(R.drawable.all_et_background_error)
            binding.etPwd.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor))
            binding.tvWrongSign.setText(R.string.login_fail)
        } else {
            binding.etPwd.setBackgroundResource(R.drawable.all_et_background_selector)
            binding.etPwd.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvWrongSign.text = ""
        }
    }

    private fun checkPwdAgain(check: Boolean, text: String) {
        if (!check) {
            binding.etPwd.setBackgroundResource(R.drawable.all_et_background_error)
            binding.etPwd.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor))
            binding.etPwdAgain.setBackgroundResource(R.drawable.all_et_background_error)
            binding.etPwdAgain.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor))
            binding.tvWrongSign.text = text
        } else {
            binding.etPwd.setBackgroundResource(R.drawable.all_et_background_selector)
            binding.etPwd.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.etPwdAgain.setBackgroundResource(R.drawable.all_et_background_selector)
            binding.etPwdAgain.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvWrongSign.text = ""
        }
    }


    private fun checkBothPwd(new: Boolean) {
        binding.etPwd.setOnEditorActionListener{ view, action, event ->
            val handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                patternVM.checkPwd(view.text.toString())
            }
            handled
        }
        binding.etPwdAgain.setOnEditorActionListener{ view, action, event ->
            val handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                patternVM.checkPwd2(view.text.toString())
            }
            handled
        }
//        binding.etPwd.addTextChangedListener(patternVM.onPwdTextWatcher())
//        binding.etPwdAgain.addTextChangedListener(patternVM.onPwd2TextWatcher())

        // 기존유저인 경우 비밀번호만 확인함
        patternVM.pwdResult.observe(viewLifecycleOwner) {
            checkPwd(it)
            setNextButtonEnable(new)
        }
        // 신규유저인 경우 재입력까지 확인해야 함
        patternVM.pwd2Result.observe(viewLifecycleOwner) { result ->
            checkPwdAgain(result, getString(R.string.login_diff_pwd))
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

    // 회원 정보 수정 --------------------------------------------------------------------------------
    private fun initEditPwdFragment() {
        with(binding) {
            tvTitle.text = getString(R.string.my_edit_pwd_sub_title)
            tvFindPwd.visibility = View.GONE
        }
    }

    private fun checkEditPwd() {
        binding.etPwd.setOnEditorActionListener{ view, action, event ->
            val handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                patternVM.checkPwd(view.text.toString())
            }
            handled
        }
        binding.etPwdAgain.setOnEditorActionListener{ view, action, event ->
            val handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                patternVM.checkPwd2(view.text.toString())
                patternVM.checkEditPwd()
            }
            handled
        }

        patternVM.pwdResult.observe(viewLifecycleOwner) { result ->
            checkPwd(result)
            Log.d("pwd_result", "$result")
            setEditButtonEnbaleCheck()
        }
        patternVM.pwd2Result.observe(viewLifecycleOwner) { result ->
            Log.d("pwd2_result", "$result")
            checkPwdAgain(result, getString(R.string.login_diff_pwd))
            if (patternVM.pwdResult.value == false && result == false) {
                // 비밀번호 형식이 잘못된 경우, 해당 사인 우선 표시
                binding.tvWrongSign.setText(R.string.login_wrong_pwd)
            }
            setEditButtonEnbaleCheck()
        }
        patternVM.pwdEditNotSame.observe(viewLifecycleOwner) { result ->
            if (patternVM.pwd2Result.value == true && patternVM.pwdResult.value == true) {
                Log.d("pwd_same_result", "$result")
                checkPwdAgain(result, getString(R.string.my_pwd_wrong_same))
            }
            setEditButtonEnbaleCheck()
        }
    }

    private fun setEditButtonEnbaleCheck() {
        binding.btnNext.isEnabled =
            (patternVM.pwdResult.value == true) && (patternVM.pwd2Result.value == true) && (patternVM.pwdEditNotSame.value == true) &&
                    (binding.etPwd.text.toString() != "") && (binding.etPwdAgain.text.toString() != "")
    }

    private fun setEditButton() {
        binding.btnNext.setOnClickListener {
            myEditVM.patchPassword(binding.etPwdAgain.text.toString())
            myEditVM.isResponse.observe(viewLifecycleOwner) { response ->
                if (response) {
                    myEditVM.initResponseValue()
                    (activity as MyEditActivity).finish()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}