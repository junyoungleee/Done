package com.palette.done.view.my.menu

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.databinding.DialogOutBinding
import com.palette.done.view.StartActivity
import com.palette.done.view.util.NetworkManager
import com.palette.done.viewmodel.*

class OutDialog(var outMode: Out) : DialogFragment() {
    private var _binding: DialogOutBinding? = null
    private val binding get() = _binding!!

    private val mode = outMode

    private val outVM: OutViewModel by activityViewModels { OutViewModelFactory(
        MemberRepository(),
        (requireActivity().application as DoneApplication).doneRepository,
        (requireActivity().application as DoneApplication).stickerRepository
    )}

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
                    btnLeft.background = ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_out_btn_round)
                    btnRight.text = getString(R.string.yes)
                    btnRight.setOnClickListener {
                        // 로그아웃 - DB 초기화
                        if (NetworkManager.checkNetworkState(requireActivity())) {
                            outVM.logOut()
                            outVM.isResponse.observe(viewLifecycleOwner) {
                                val intent = Intent(requireActivity(), StartActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                dismiss()
                                startActivity(intent)
                            }
                        } else {
                            NetworkManager.showRequireNetworkToast(requireActivity())
                        }
                    }
                   btnLeft.setOnClickListener {
                        dismiss()
                    }
                }
            }
            Out.QUIT -> {
                with(binding) {
                    tvOutTitle.text = getString(R.string.my_dialog_quit)
                    tvOutDetail.visibility = View.VISIBLE
                    btnRight.text = getString(R.string.no)
                    btnRight.background = ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_out_btn_round)
                    btnLeft.text = getString(R.string.yes)
                    btnRight.setOnClickListener {
                        dismiss()
                    }
                    btnLeft.setOnClickListener {
                        // 탈퇴
                        if (NetworkManager.checkNetworkState(requireActivity())) {
                            outVM.deleteMemberInServer()
                            outVM.isResponse.observe(viewLifecycleOwner) {
                                if (it) {
                                    val intent = Intent(requireActivity(), StartActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    dismiss()
                                    startActivity(intent)
                                }
                            }
                        } else {
                            NetworkManager.showRequireNetworkToast(requireActivity())
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class Out {
    LOG_OUT, QUIT
}