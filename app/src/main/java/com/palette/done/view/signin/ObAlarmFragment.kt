package com.palette.done.view.signin

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.palette.done.R
import com.palette.done.databinding.FragmentObAlarmBinding
import com.palette.done.databinding.FragmentObTypeBinding

class ObAlarmFragment : Fragment() {

    private var _binding: FragmentObAlarmBinding? = null
    private val binding get() = _binding!!
    private var activity: OnBoardingActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentObAlarmBinding.inflate(inflater, container, false)

        setNextButton()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as OnBoardingActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    private fun setNextButton() {

    }

}