package com.palette.done.view.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.palette.done.R
import com.palette.done.databinding.FragmentObAlarmBinding
import com.palette.done.databinding.FragmentObTypeBinding
import com.palette.done.view.MainActivity

class ObAlarmFragment : Fragment() {

    private var _binding: FragmentObAlarmBinding? = null
    private val binding get() = _binding!!

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

    private fun setNextButton() {
        // 기존 스택 없애고 메인 화면이 루트
        binding.btnNext.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}