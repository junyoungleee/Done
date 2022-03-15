package com.palette.done.view.decoration

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import com.palette.done.R
import com.palette.done.databinding.ToastDoneBinding
import com.palette.done.view.util.Util

object DoneToast {

    val util = Util()

    fun createToast(context: Context, doneContent: String, cheerMsg: String): Toast? {
        val inflater = LayoutInflater.from(context)
        val binding: ToastDoneBinding = DataBindingUtil.inflate(inflater, R.layout.toast_done, null, false)

        binding.tvToastTitle.text = "[$doneContent]"
        binding.tvCheerUpMsg.text = cheerMsg

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            duration = Toast.LENGTH_SHORT
            view = binding.root
        }
    }
}