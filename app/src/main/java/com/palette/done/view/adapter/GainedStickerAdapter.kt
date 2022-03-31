package com.palette.done.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.data.db.entity.Category
import com.palette.done.data.db.entity.Sticker
import com.palette.done.databinding.ItemGainedStickerBinding

class GainedStickerAdapter(val context: Context): ListAdapter<Sticker,
        GainedStickerAdapter.GainedStickerViewHolder>(StickerComparator()) {

    private lateinit var stickerClickedListener: OnStickerClickListener

    class GainedStickerViewHolder(val binding: ItemGainedStickerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GainedStickerViewHolder {
        val binding = ItemGainedStickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GainedStickerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GainedStickerViewHolder, position: Int) {
        val sticker = getItem(position)
        with(holder.binding) {
            val id = sticker.stickerNo
            val name = "sticker_$id"
            val imgId = context.resources.getIdentifier(name, "drawable", context.packageName)
            ivGainedSticker.setImageDrawable(ContextCompat.getDrawable(context, imgId))
            ivGainedSticker.setOnClickListener {
                stickerClickedListener.onStickerClick(it, sticker)
            }
        }
    }

    interface OnStickerClickListener {
        fun onStickerClick(v: View, sticker: Sticker)
    }

    fun setCategoryClickListener(onStickerClickListener: OnStickerClickListener) {
        this.stickerClickedListener = onStickerClickListener
    }

    private class StickerComparator: DiffUtil.ItemCallback<Sticker>() {
        override fun areItemsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
            return oldItem.stickerNo == newItem.stickerNo
        }

    }


}