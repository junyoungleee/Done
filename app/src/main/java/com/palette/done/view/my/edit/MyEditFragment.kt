package com.palette.done.view.my.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.palette.done.databinding.FragmentMyEditBinding

class MyEditFragment : Fragment() {

    private var _binding: FragmentMyEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? {
        _binding = FragmentMyEditBinding.inflate(inflater, container, false)

        showOutDialog()
        return binding.root
    }

    private fun showOutDialog() {
        binding.tvLogOut.setOnClickListener {
            val dialog = OutDialog(Out.LOG_OUT)
            dialog.show(requireActivity().supportFragmentManager, "OutDialog")
        }
        binding.tvQuit.setOnClickListener {
            val dialog = OutDialog(Out.QUIT)
            dialog.show(requireActivity().supportFragmentManager, "OutDialog")
        }
    }

}