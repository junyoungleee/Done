package com.palette.done.view.my.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.palette.done.DoneApplication
import com.palette.done.databinding.FragmentMyEditBinding
import com.palette.done.view.my.MyEditActivity
import com.palette.done.view.my.MyEditMode

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
        val intent = Intent(requireActivity(), MyEditActivity::class.java)
        with(binding) {
            tvNicknameEdit.setOnClickListener {
                // 닉네임 수정화면 연결
                intent.putExtra("mode", MyEditMode.NICKNAME.name)
                startActivity(intent)
            }
            llType.setOnClickListener {
                // 기록 유형 변경 화면
                intent.putExtra("mode", MyEditMode.TPYE.name)
                startActivity(intent)
            }
            llPwdChange.setOnClickListener {
                // 비밀번호 변경 화면
                intent.putExtra("mode", MyEditMode.PWD.name)
                startActivity(intent)
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

    override fun onResume() {
        super.onResume()
        // 닉네임 업데이트
        binding.tvNickname.text = DoneApplication.pref.nickname
    }

}