package com.palette.done.view.my.sticker

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.palette.done.data.db.entity.Sticker
import com.palette.done.databinding.DialogStickerInfoBinding

class StickerInfoDialog(val sticker: Sticker) : DialogFragment() {

    private var _binding: DialogStickerInfoBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogStickerInfoBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(0.7F)

        with(binding) {
            val stickerId = requireActivity().resources.getIdentifier("sticker_${sticker.stickerNo}", "drawable", requireActivity().packageName)
            ivSticker.setImageDrawable(ContextCompat.getDrawable(requireActivity(), stickerId))
            tvStickerName.text = sticker.name
            tvStickerDetail.text = sticker.explanation
            btnOk.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
