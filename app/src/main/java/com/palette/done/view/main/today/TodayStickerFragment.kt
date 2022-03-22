package com.palette.done.view.main.today

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.FragmentDoneBinding
import com.palette.done.databinding.FragmentTodayStickerBinding
import com.palette.done.viewmodel.TodayRecordViewModel
import com.palette.done.viewmodel.TodayRecordViewModelFactory
import com.palette.done.viewmodel.TodayStickerViewModel

class TodayStickerFragment : Fragment() {

    private var _binding: FragmentTodayStickerBinding? = null
    private val binding get() = _binding!!

    private val stickerVM : TodayStickerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTodayStickerBinding.inflate(inflater, container, false)

        binding.llSticker.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DoneApplication.pref.keyboard)



        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}