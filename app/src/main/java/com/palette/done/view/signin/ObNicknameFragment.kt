package com.palette.done.view.signin

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.FragmentObNicknameBinding
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.viewmodel.OnBoardingViewModel
import com.palette.done.viewmodel.OnBoardingViewModelFactory

class ObNicknameFragment : Fragment() {

    private var _binding: FragmentObNicknameBinding? = null
    private val binding get() = _binding!!

    private val onBoardingVM: OnBoardingViewModel by activityViewModels { OnBoardingViewModelFactory(
        MemberRepository()
    ) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentObNicknameBinding.inflate(inflater, container, false)

        setNextButton()

        binding.root.setOnClickListener {
            hideKeyboard()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setNextButton() {
        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                binding.btnNext.isEnabled = !s.equals("")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.btnNext.isEnabled = !s.equals("")
            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnNext.isEnabled = !s!!.equals("")

            }
        })
        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager_on_boarding)
        binding.btnNext.setOnClickListener {
            onBoardingVM.nickname.value = binding.etNickname.text.toString()
            Log.d("onBoarding_nick","${onBoardingVM.nickname.value}")
            viewPager?.currentItem = 1
            setIndicator()  // 다음 버튼 클릭 시 indicator 수정
        }
    }

    private fun setIndicator() {
        val indicator2 = activity?.findViewById<ImageView>(R.id.iv_indicator_second)
        val indicator3 = activity?.findViewById<ImageView>(R.id.iv_indicator_third)

        indicator2!!.setImageResource(R.drawable.ic_indicator_now)
        indicator3!!.setImageResource(R.drawable.ic_indicator_left)
    }

    private fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

}