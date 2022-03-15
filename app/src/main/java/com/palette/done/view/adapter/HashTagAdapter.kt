package com.palette.done.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.data.db.entity.Routine
import com.palette.done.data.remote.model.dones.Tags
import com.palette.done.databinding.ItemTagBinding

class HashTagAdapter : ListAdapter<Tags, HashTagAdapter.HashTagViewHolder>(TagComparator()){
    private lateinit var tagItemClickListener: OnTagItemClickListener
    private var clickedPosition: Int? = null

    fun initClickedPosition() {
        clickedPosition = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashTagViewHolder {
        val binding = ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HashTagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HashTagViewHolder, position: Int) {
        val hashTag = getItem(position)
        with(holder.binding) {
            tvTag.text = "#${hashTag.name}"
            tvTag.setOnClickListener {
                clickedPosition = position
                tagItemClickListener.onTagClick(it, hashTag)
            }
            tvTag.isEnabled = clickedPosition != position
        }
    }

    class HashTagViewHolder(val binding: ItemTagBinding): RecyclerView.ViewHolder(binding.root) { }

    private class TagComparator: DiffUtil.ItemCallback<Tags>() {
        override fun areItemsTheSame(oldItem: Tags, newItem: Tags): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Tags, newItem: Tags): Boolean {
            return oldItem.tag_no == newItem.tag_no
        }
    }

    interface OnTagItemClickListener {
        fun onTagClick(v: View, hashTag: Tags)
    }

    fun setTagItemClickListener(onRoutineItemClickListener: OnTagItemClickListener) {
        this.tagItemClickListener = onRoutineItemClickListener
    }
}