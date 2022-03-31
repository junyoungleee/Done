package com.palette.done.view.main.notice

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.DialogFirstVisitBinding
import com.palette.done.databinding.DialogGuideDoneBinding
import java.time.LocalDate

class FirstVisitDoneDialog : DialogFragment() {
    private var _binding: DialogGuideDoneBinding? = null
    private val binding get() = _binding!!

    private val type = DoneApplication.pref.type

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogGuideDoneBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(0.7F)

        binding.tvClose.setOnClickListener {

            dismiss()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}