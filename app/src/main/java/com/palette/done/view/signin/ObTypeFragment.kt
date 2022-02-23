package com.palette.done.view.signin

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.palette.done.R
import com.palette.done.databinding.FragmentObNicknameBinding
import com.palette.done.databinding.FragmentObTypeBinding
import com.palette.done.viewmodel.OnBoardingViewModel

class ObTypeFragment : Fragment() {

    private var _binding: FragmentObTypeBinding? = null
    private val binding get() = _binding!!

    private val onBoardingVM: OnBoardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentObTypeBinding.inflate(inflater, container, false)

        onBoardingVM.nickname.observe(viewLifecycleOwner) {
            binding.tvNickname.text = onBoardingVM.nickname.value.toString()
        }

        setTypeButton()
        setNextButton()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setTypeButton() {
        binding.llTypeJ.setOnClickListener {
            onBoardingVM.userType.value = "j"
            binding.btnNext.isEnabled = true
            binding.llTypeP.isSelected = false
            binding.llTypeJ.isSelected = true
        }
        binding.llTypeP.setOnClickListener {
            onBoardingVM.userType.value = "p"
            binding.btnNext.isEnabled = true
            binding.llTypeP.isSelected = true
            binding.llTypeJ.isSelected = false
        }
    }

    private fun setNextButton() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager_on_boarding)
        binding.btnNext.setOnClickListener {
            viewPager?.currentItem = 2
            setIndicator() // 다음 버튼 클릭 시 indicator 수정
        }
    }

    private fun setIndicator() {
        val indicator2 = activity?.findViewById<ImageView>(R.id.iv_indicator_second)
        val indicator3 = activity?.findViewById<ImageView>(R.id.iv_indicator_third)

        indicator2!!.setImageResource(R.drawable.ic_indicator_now)
        indicator3!!.setImageResource(R.drawable.ic_indicator_now)
    }

}