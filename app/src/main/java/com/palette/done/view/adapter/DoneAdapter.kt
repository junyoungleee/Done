package com.palette.done.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.data.db.entity.Done
import com.palette.done.databinding.ItemDoneBinding

class DoneAdapter: ListAdapter<Done, DoneAdapter.DoneViewHolder>(DoneComparator())  {

    class DoneViewHolder(val binding: ItemDoneBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(done: Done) {
            binding.tvDoneContent.text = done.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoneViewHolder {
        val binding = ItemDoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoneViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    private class DoneComparator: DiffUtil.ItemCallback<Done>() {
        override fun areItemsTheSame(oldItem: Done, newItem: Done): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Done, newItem: Done): Boolean {
            return oldItem.doneId == newItem.doneId
        }

    }
}