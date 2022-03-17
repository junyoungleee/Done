package com.palette.done.view.my.edit

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.palette.done.R
import com.palette.done.databinding.DialogOutBinding

class OutDialog(var outMode: Out) : DialogFragment() {
    private var _binding: DialogOutBinding? = null
    private val binding get() = _binding!!

    private val mode = outMode

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogOutBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(0.7F)

        when(mode) {
            Out.LOG_OUT -> {
                with(binding) {
                    tvOutTitle.text = getString(R.string.my_dialog_log_out)
                    tvOutDetail.visibility = View.GONE
                    btnLeft.text = getString(R.string.no)
                    btnRight.text = getString(R.string.yes)
                    btnRight.setOnClickListener {

                    }
                }
            }
            Out.QUIT -> {
                with(binding) {
                    tvOutTitle.text = getString(R.string.my_dialog_log_out)
                    tvOutDetail.visibility = View.VISIBLE
                    btnRight.text = getString(R.string.no)
                    btnLeft.text = getString(R.string.yes)
                    btnLeft.setOnClickListener {

                    }
                }
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}

enum class Out {
    LOG_OUT, QUIT
}