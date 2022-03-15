package com.palette.done.view.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.palette.done.R
import com.palette.done.databinding.FragmentLoginEmailBinding
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.viewmodel.LoginViewModel
import com.palette.done.viewmodel.LoginViewModelFactory
import com.palette.done.viewmodel.PatternCheckViewModel

class LoginEmailFragment : Fragment() {

    private var _binding: FragmentLoginEmailBinding? = null
    private val binding get() = _binding!!

    private val loginVM : LoginViewModel by activityViewModels { LoginViewModelFactory(
        MemberRepository()
    ) }
    private val patternVM : PatternCheckViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginEmailBinding.inflate(inflater, container, false)

        // 다음 fragment 이동
        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager_login)
        binding.btnNext.setOnClickListener {
            loginVM.postEmailCheck(binding.etEmail.text.toString())
            loginVM.isResponse.observe(viewLifecycleOwner) { is_response ->
                if (is_response) {
                    Log.d("loginVM_is_response", "$is_response")
                    viewPager?.currentItem = 1
                }
            }
        }
        setNextButtonEnable()

        return binding.root
    }

    private fun checkEmail(check: Boolean) {
        if (!check) {
            binding.etEmail.setBackgroundResource(R.drawable.all_et_background_error)
            binding.etEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor))
            binding.tvWrongSign.setText(R.string.login_wrong_email)
        } else {
            binding.etEmail.setBackgroundResource(R.drawable.all_et_background_selector)
            binding.etEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvWrongSign.text = ""
        }
    }

    private fun setNextButtonEnable() {
        binding.etEmail.addTextChangedListener(patternVM.onEmailTextWatcher())
        patternVM.emailResult.observe(viewLifecycleOwner) {
            checkEmail(it)
            binding.btnNext.isEnabled = (patternVM.emailResult.value == true) && (binding.etEmail.text.toString() != "")
        }
    }

    override fun onResume() {
        super.onResume()
        loginVM.isResponse.value = false
        Log.d("loginVM_on_pause", "${loginVM.isResponse.value}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}