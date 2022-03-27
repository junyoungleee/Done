package com.palette.done.view.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.DialogFirstVisitBinding
import java.time.LocalDate

class FirstVisitDialog() : DialogFragment() {
    private var _binding: DialogFirstVisitBinding? = null
    private val binding get() = _binding!!

    private val type = DoneApplication.pref.type

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogFirstVisitBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(0.7F)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnDoneWrite.setOnClickListener {
            // 던리스트 작성 버튼
            val intent = Intent(requireActivity(), DoneActivity::class.java)
            val date = LocalDate.now().toString()
            intent.putExtra("clickedDate", date)
            dismiss()
            startActivity(intent)
        }

        if (type == "j") {
            setTypeJ()
        } else {
            setTypeP()
        }

        return binding.root
    }

    private fun setTypeJ() {
        with(binding) {
            tvPlan.setOnClickListener {
                val intent = Intent(requireActivity(), GuidePlanActivity::class.java)
                startActivity(intent)
            }
            btnPlanWrite.setOnClickListener {
                val intent = Intent(requireActivity(), PlanRoutineActivity::class.java)
                val date = LocalDate.now().toString()
                intent.putExtra("mode", ItemMode.PLAN.name)
                intent.putExtra("date", date)
                dismiss()
                startActivity(intent)
            }
        }
    }

    private fun setTypeP() {
        with(binding) {
            tvDialogTitle.text = getString(R.string.main_dialog_p_title)
            ivType.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.img_dd_type_p))
            tvPlan.visibility = View.GONE
            btnPlanWrite.visibility = View.GONE
            btnDoneWrite.text = getString(R.string.main_dialog_p_btn_done)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}