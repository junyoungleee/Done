package com.palette.done.view.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.R
import com.palette.done.data.db.entity.Sticker
import com.palette.done.databinding.ItemStickerBinding

class StickerAdapter(val context: Context): ListAdapter<Sticker, StickerAdapter.StickerViewHolder>(StickerComparator()){

    private lateinit var stickerClickListener: OnStickerClickListener

    class StickerViewHolder(val binding: ItemStickerBinding): RecyclerView.ViewHolder(binding.root) { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val binding = ItemStickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StickerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        val sticker = getItem(position)
        with(holder.binding) {
            tvStickerName.text = sticker.name
            val stickerId = context.resources.getIdentifier("sticker_${sticker.stickerNo}", "drawable", context.packageName)
            ivSticker.setImageDrawable(ContextCompat.getDrawable(context, stickerId))
            if (sticker.get) {
                // 획득한 스티커
                ivSticker.imageTintList = null
            }
            root.setOnClickListener {
                stickerClickListener.onStickerClick(it, sticker)
            }
        }
    }

    private class StickerComparator: DiffUtil.ItemCallback<Sticker>() {
        override fun areItemsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
            return oldItem.get == newItem.get
        }
    }

    interface OnStickerClickListener {
        fun onStickerClick(v: View, sticker: Sticker)
    }

    fun setStickerClickListener(onStickerClickListener: OnStickerClickListener) {
        this.stickerClickListener = onStickerClickListener
    }

}