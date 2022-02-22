package com.palette.done.view.signin

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.palette.done.R
import com.palette.done.databinding.FragmentObNicknameBinding
import com.palette.done.databinding.FragmentObTypeBinding

class ObTypeFragment : Fragment() {

    private var _binding: FragmentObTypeBinding? = null
    private val binding get() = _binding!!

    private var type: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentObTypeBinding.inflate(inflater, container, false)

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
            type = 1
            binding.btnNext.isEnabled = true
            binding.llTypeP.isSelected = false
            binding.llTypeJ.isSelected = true
        }
        binding.llTypeP.setOnClickListener {
            type = 2
            binding.btnNext.isEnabled = true
            binding.llTypeP.isSelected = true
            binding.llTypeJ.isSelected = false
        }
    }

    private fun setNextButton() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager_on_boarding)
        binding.btnNext.setOnClickListener {
            viewPager?.currentItem = 2
        }
    }

}