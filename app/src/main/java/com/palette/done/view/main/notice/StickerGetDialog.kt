package com.palette.done.view.main.notice

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.palette.done.DoneApplication
import com.palette.done.data.remote.model.sticker.Stickers
import com.palette.done.data.remote.repository.StickerServerRepository
import com.palette.done.databinding.DialogStickerGetBinding
import com.palette.done.viewmodel.MainStickerViewModel
import com.palette.done.viewmodel.MainStickerViewModelFactory

class StickerGetDialog(val sticker: Stickers) : DialogFragment() {
    private var _binding: DialogStickerGetBinding? = null
    private val binding get() = _binding!!

    private val stickerVM: MainStickerViewModel by activityViewModels() {
        MainStickerViewModelFactory(StickerServerRepository(),
            (requireActivity().application as DoneApplication).stickerRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DialogStickerGetBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(0.7F)

        initDialog()
        getSticker()

        return binding.root
    }

    private fun initDialog() {
        with(binding) {
            tvStickerName.text = sticker.name
            tvStickerDetail.text = sticker.explanation
            val stickerId = requireActivity().resources.getIdentifier(
                "sticker_${sticker.sticker_no}", "drawable", requireActivity().packageName)
            ivSticker.setImageDrawable(ContextCompat.getDrawable(requireActivity(), stickerId))
        }
    }

    private fun getSticker() {
        binding.btnGet.setOnClickListener {
            // 획득하기 POST & DB 저장
            stickerVM.postGainedSticker(sticker.sticker_no)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}