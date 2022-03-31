package com.palette.done.view.my.sticker

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.palette.done.DoneApplication
import com.palette.done.data.db.entity.Sticker
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.data.remote.repository.StickerServerRepository
import com.palette.done.databinding.ActivityStickerBinding
import com.palette.done.view.adapter.StickerAdapter
import com.palette.done.view.decoration.GridLayoutDecoration
import com.palette.done.view.decoration.RecyclerViewDecoration
import com.palette.done.view.my.menu.Out
import com.palette.done.view.my.menu.OutDialog
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.*

class StickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStickerBinding

    private val stickerVM: StickerViewModel by viewModels() {
        StickerViewModelFactory(StickerServerRepository(), DoneApplication().stickerRepository)
    }

    private var totalWidth: Int = 0
    private var decoWidth: Int = 0
    private val util = Util()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stickerVM.getAllGainedSticker()
        binding.btnBack.setOnClickListener {
            finish()
        }

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        totalWidth = size.x
        decoWidth = (totalWidth - (util.dpToPx(90)*3) - (util.dpToPx(20)*2))/2

        initType1Recyclerview()
        initType2Recyclerview()
        initType3Recyclerview()
    }

    private fun initType1Recyclerview() {
        // 공통
        val sticker1Adapter = StickerAdapter(this)
        with(binding.rcStickerType1) {
            adapter = sticker1Adapter
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(GridLayoutDecoration(3, decoWidth))
            itemAnimator = null
        }
        stickerVM.type1Stickers.observe(this) { stickers ->
            Log.d("sticker_type1", "${stickers.size}")
            stickers.let { sticker1Adapter.submitList(it) }
        }
        sticker1Adapter.setStickerClickListener(object: StickerAdapter.OnStickerClickListener {
            override fun onStickerClick(v: View, sticker: Sticker) {
                // 스티커 설명 다이얼로그
                val dialog = StickerInfoDialog(sticker)
                dialog.show(this@StickerActivity.supportFragmentManager, "StickerInfoDialog")
            }
        })
    }

    private fun initType2Recyclerview() {
        // 프로기록러
        val sticker2Adapter = StickerAdapter(this)
        with(binding.rcStickerType2) {
            adapter = sticker2Adapter
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(GridLayoutDecoration(3, decoWidth))
            itemAnimator = null
        }
        stickerVM.type2Stickers.observe(this) { stickers ->
            stickers.let { sticker2Adapter.submitList(it) }
        }
        sticker2Adapter.setStickerClickListener(object: StickerAdapter.OnStickerClickListener {
            override fun onStickerClick(v: View, sticker: Sticker) {
                // 스티커 설명 다이얼로그
//                val dialog = StickerInfoDialog(sticker)
//                dialog.show(this@StickerActivity.supportFragmentManager, "StickerInfoDialog")
            }
        })
    }

    private fun initType3Recyclerview() {
        // 스페셜
        val sticker3Adapter = StickerAdapter(this)
        with(binding.rcStickerType3) {
            adapter = sticker3Adapter
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(GridLayoutDecoration(3, decoWidth))
            itemAnimator = null
        }
        stickerVM.type3Stickers.observe(this) { stickers ->
            stickers.let { sticker3Adapter.submitList(it) }
        }
        sticker3Adapter.setStickerClickListener(object: StickerAdapter.OnStickerClickListener {
            override fun onStickerClick(v: View, sticker: Sticker) {
                // 스티커 설명 다이얼로그
//                val dialog = StickerInfoDialog(sticker)
//                dialog.show(this@StickerActivity.supportFragmentManager, "StickerInfoDialog")
            }
        })
    }
}