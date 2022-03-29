package com.palette.done.view.main.today

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.datasource.StickerRepository
import com.palette.done.data.db.entity.Sticker
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.FragmentDoneBinding
import com.palette.done.databinding.FragmentTodayStickerBinding
import com.palette.done.view.adapter.GainedStickerAdapter
import com.palette.done.view.decoration.GridLayoutDecoration
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.TodayRecordViewModel
import com.palette.done.viewmodel.TodayRecordViewModelFactory
import com.palette.done.viewmodel.TodayStickerViewModel
import com.palette.done.viewmodel.TodayStickerViewModelFactory

class TodayStickerFragment : Fragment() {

    private var _binding: FragmentTodayStickerBinding? = null
    private val binding get() = _binding!!

    private val stickerVM : TodayStickerViewModel by activityViewModels() {
        TodayStickerViewModelFactory(DoneApplication().stickerRepository)
    }

    private lateinit var stickerAdapter: GainedStickerAdapter
    private val util = Util()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTodayStickerBinding.inflate(inflater, container, false)

        binding.llSticker.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DoneApplication.pref.keyboard)
        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        stickerAdapter = GainedStickerAdapter(requireActivity())

        stickerVM.gainedSticker.observe(viewLifecycleOwner) { stickers ->
            if (stickers.isEmpty()) {
                binding.llStickerEmpty.visibility = View.VISIBLE
                binding.rcStickerGained.visibility = View.GONE
            } else {
                binding.llStickerEmpty.visibility = View.GONE
                binding.rcStickerGained.visibility = View.VISIBLE
                stickerAdapter.submitList(stickers)
            }
        }
        binding.rcStickerGained.adapter = stickerAdapter
        binding.rcStickerGained.layoutManager = GridLayoutManager(context, 3)
        binding.rcStickerGained.addItemDecoration(GridLayoutDecoration(3, util.dpToPx(18)))
        stickerAdapter.setCategoryClickListener(object: GainedStickerAdapter.OnStickerClickListener {
            override fun onStickerClick(v: View, sticker: Sticker) {
                stickerVM.setTodaySticker(sticker.stickerNo)
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}