package com.palette.done.view.my.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.palette.done.DoneApplication
import com.palette.done.databinding.FragmentMyEditBinding

class MyEditFragment : Fragment() {

    private var _binding: FragmentMyEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? {
        _binding = FragmentMyEditBinding.inflate(inflater, container, false)

        binding.tvNickname.text = DoneApplication.pref.nickname

        setButtonsDestination()
        showOutDialog()

        return binding.root
    }

    private fun setButtonsDestination() {
        with(binding) {
            tvNicknameEdit.setOnClickListener {
                // 닉네임 수정화면 연결
            }
            btnEditType.setOnClickListener {

            }
            btnEditPwd.setOnClickListener {

            }
        }
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