package com.palette.done.view.main.today

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.FragmentDoneBinding
import com.palette.done.databinding.FragmentTodayRecordBinding

class TodayRecordFragment : Fragment() {

    private var _binding: FragmentTodayRecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTodayRecordBinding.inflate(inflater, container, false)

        binding.llSticker.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DoneApplication.pref.keyboard)


        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}