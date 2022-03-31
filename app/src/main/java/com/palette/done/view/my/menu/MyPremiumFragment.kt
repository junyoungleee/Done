package com.palette.done.view.my.menu

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.palette.done.R
import com.palette.done.databinding.FragmentMyGradeBinding
import com.palette.done.databinding.FragmentMyPremiumBinding

class MyPremiumFragment : Fragment() {

    private var _binding: FragmentMyPremiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPremiumBinding.inflate(inflater, container, false)

        SpannableStringBuilder(getString(R.string.premium_later)).apply {
            setSpan(
                StyleSpan(Typeface.BOLD), 15, 29, Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            binding.tvLater.text = this
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}