package com.palette.done.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
import com.palette.done.view.util.NetworkManager
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

        binding.root.setOnClickListener {
            hideKeyboard()
        }

        return binding.root
    }

    private fun setButtonsDestination(new: Boolean) {
        lateinit var intent: Intent
        // ???????????? ????????? ??????
        binding.tvFindPwd.setOnClickListener {
            intent = Intent(requireContext(), FindPwdActivity::class.java)
            startActivity(intent)
        }
        binding.btnNext.setOnClickListener {
            if (NetworkManager.checkNetworkState(requireActivity())) {
                if (new) {
                    // ?????? ??????????????? ?????????
                    intent = Intent(requireContext(), OnBoardingActivity::class.java)
                    loginVM.postSignUp(binding.etPwd.text.toString())
                    DoneApplication.pref.signup = "false"

                    // ?????? ???????????? ?????? ?????? ??????
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    loginVM.isSignUpSuccess.observe(viewLifecycleOwner) {
                        if (loginVM.isSignUpSuccess.value!!) {
                            startActivity(intent)
                        }
                    }
                    startActivity(intent)
                } else {
                    // ?????? ??????????????? ????????????
                    intent = Intent(requireContext(), MainActivity::class.java)
                    loginVM.postLogin(binding.etPwd.text.toString())

                    // ?????? ???????????? ?????? ?????? ??????
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    // ????????? ????????? ????????? response ?????? ????????????
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
            } else {
                NetworkManager.showRequireNetworkToast(requireActivity())
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
//        binding.etPwd.setOnEditorActionListener{ view, action, event ->
//            val handled = false
//            if (action == EditorInfo.IME_ACTION_DONE) {
//                patternVM.checkPwd(view.text.toString())
//            }
//            handled
//        }
//        binding.etPwdAgain.setOnEditorActionListener{ view, action, event ->
//            val handled = false
//            if (action == EditorInfo.IME_ACTION_DONE) {
//                patternVM.checkPwd2(view.text.toString())
//            }
//            handled
//        }

        // 1.2 ????????????
        binding.etPwd.addTextChangedListener(patternVM.onPwdTextWatcher())
        binding.etPwdAgain.addTextChangedListener(patternVM.onPwd2TextWatcher())

        // ??????????????? ?????? ??????????????? ?????????
        patternVM.pwdResult.observe(viewLifecycleOwner) {
            checkPwd(it)
            setNextButtonEnable(new)
        }
        // ??????????????? ?????? ??????????????? ???????????? ???
        patternVM.pwd2Result.observe(viewLifecycleOwner) { result ->
            checkPwdAgain(result, getString(R.string.login_diff_pwd))
            if (patternVM.pwdResult.value == false && result == false) {
                // ???????????? ????????? ????????? ??????, ?????? ?????? ?????? ??????
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
            // ?????? ?????? : ??????X
            activity?.findViewById<TextView>(R.id.tv_login_pwd_title)!!.text = resources.getString(R.string.login_title_sign_up)
            binding.tvTitle.text = getString(R.string.login_title_pwd_new)
            binding.etPwdAgain.visibility = View.VISIBLE
            binding.tvFindPwd.visibility = View.GONE
        } else {
            // ?????? ?????? : ??????
            activity?.findViewById<TextView>(R.id.tv_login_pwd_title)!!.text = resources.getString(R.string.login_title_login)
            binding.tvTitle.text = getString(R.string.login_title_pwd_old)
            binding.etPwdAgain.visibility = View.GONE
            binding.tvFindPwd.visibility = View.VISIBLE
        }
    }

    // ?????? ?????? ?????? --------------------------------------------------------------------------------
    private fun initEditPwdFragment() {
        with(binding) {
            tvTitle.text = getString(R.string.my_edit_pwd_sub_title)
            tvFindPwd.visibility = View.GONE
        }
    }

    private fun checkEditPwd() {
//        binding.etPwd.setOnEditorActionListener{ view, action, event ->
//            val handled = false
//            if (action == EditorInfo.IME_ACTION_DONE) {
//                patternVM.checkPwd(view.text.toString())
//            }
//            handled
//        }
//        binding.etPwdAgain.setOnEditorActionListener{ view, action, event ->
//            val handled = false
//            if (action == EditorInfo.IME_ACTION_DONE) {
//                patternVM.checkPwd2(view.text.toString())
//                patternVM.checkEditPwd()
//            }
//            handled
//        }

        // 1.2 ????????????
        binding.etPwd.addTextChangedListener(patternVM.onEditPwdTextWatcher())
        binding.etPwdAgain.addTextChangedListener(patternVM.onPwd2TextWatcher())

        patternVM.pwdResult.observe(viewLifecycleOwner) { result ->
            checkPwd(result)
            Log.d("pwd_result", "$result")
            setEditButtonEnableCheck()
        }
        patternVM.pwd2Result.observe(viewLifecycleOwner) { result ->
            Log.d("pwd2_result", "$result")
            checkPwdAgain(result, getString(R.string.login_diff_pwd))
            if (patternVM.pwdResult.value == false && patternVM.pwdEditNotSame.value == true && result == false) {
                // ???????????? ????????? ????????? ??????, ?????? ?????? ?????? ??????
                binding.tvWrongSign.setText(R.string.login_wrong_pwd)
            }
            setEditButtonEnableCheck()
        }
        patternVM.pwdEditNotSame.observe(viewLifecycleOwner) { result ->
            if (result == false) {
                Log.d("pwd_same_result", "$result")
                checkPwdAgain(result, getString(R.string.my_pwd_wrong_same))
            }
            setEditButtonEnableCheck()
        }
    }

    private fun setEditButtonEnableCheck() {
        binding.btnNext.isEnabled =
            (patternVM.pwdResult.value == true) && (patternVM.pwd2Result.value == true) && (patternVM.pwdEditNotSame.value == true) &&
                    (binding.etPwd.text.toString() != "") && (binding.etPwdAgain.text.toString() != "")
    }

    private fun setEditButton() {
        binding.btnNext.setOnClickListener {
            if (NetworkManager.checkNetworkState(requireActivity())) {
                myEditVM.patchPassword(binding.etPwdAgain.text.toString())
                myEditVM.isResponse.observe(viewLifecycleOwner) { response ->
                    if (response) {
                        myEditVM.initResponseValue()
                        (activity as MyEditActivity).finish()
                    }
                }
            } else {
                NetworkManager.showRequireNetworkToast(requireActivity())
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    private fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}