package com.palette.done.view.login

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.FragmentLoginEmailBinding
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.view.util.NetworkManager
import com.palette.done.viewmodel.LoginViewModel
import com.palette.done.viewmodel.LoginViewModelFactory
import com.palette.done.viewmodel.PatternCheckViewModel

class LoginEmailFragment : Fragment() {

    private var _binding: FragmentLoginEmailBinding? = null
    private val binding get() = _binding!!

    private val loginVM : LoginViewModel by activityViewModels { LoginViewModelFactory(
        MemberRepository(), DoneServerRepository(), (requireActivity().application as DoneApplication).doneRepository
    ) }
    private val patternVM : PatternCheckViewModel by viewModels()

    private var rootHeight = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginEmailBinding.inflate(inflater, container, false)

        setKeyboardHeight()

        // 다음 fragment 이동
        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager_login)
        binding.btnNext.setOnClickListener {
            if (NetworkManager.checkNetworkState(requireActivity())) {
                loginVM.postEmailCheck(binding.etEmail.text.toString())
                loginVM.isResponse.observe(viewLifecycleOwner) { is_response ->
                    if (is_response) {
                        Log.d("loginVM_is_response", "$is_response")
                        viewPager?.currentItem = 1
                    }
                }
            } else {
                NetworkManager.checkNetworkState(requireActivity())
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
        binding.etEmail.setOnEditorActionListener{ view, action, event ->
            val handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                patternVM.checkEmail(view.text.toString())
            }
            handled
        }
//        binding.etEmail.addTextChangedListener(patternVM.onEmailTextWatcher())
        patternVM.emailResult.observe(viewLifecycleOwner) {
            checkEmail(it)
            binding.btnNext.isEnabled = (patternVM.emailResult.value == true) && (binding.etEmail.text.toString() != "")
        }
    }

    private fun setKeyboardHeight() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                if (rootHeight == -1) rootHeight = binding.root.height
                val visibleFrameSize = Rect()
                binding.root.getWindowVisibleDisplayFrame(visibleFrameSize)
                val heightExceptKeyboard = visibleFrameSize.bottom - visibleFrameSize.top
                val keyboard = rootHeight - heightExceptKeyboard
                Log.d("keyboard", "$keyboard")
                if (DoneApplication.pref.keyboard != keyboard && keyboard != 0) {
                    DoneApplication.pref.keyboard = keyboard
                    Log.d("keyboard_pref", "${DoneApplication.pref.keyboard}")
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    if (keyboard != 0) {
                        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            }
        })
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