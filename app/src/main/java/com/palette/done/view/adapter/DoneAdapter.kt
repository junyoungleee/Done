package com.palette.done.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.data.db.entity.Done
import com.palette.done.databinding.ItemDoneBinding

/**
 * ActivityDone
 *  던리스트 Recyclerview Adapter
 */
class DoneAdapter: ListAdapter<Done, DoneAdapter.DoneViewHolder>(DoneComparator())  {

    private lateinit var doneMenuClickListener: OnDoneMenuClickListener

    class DoneViewHolder(val binding: ItemDoneBinding): RecyclerView.ViewHolder(binding.root) { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoneViewHolder {
        val binding = ItemDoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoneViewHolder, position: Int) {
        val done = getItem(position)
        with(holder.binding) {
            tvDoneContent.text = done.content
            btnDoneMenu.setOnClickListener {
                doneMenuClickListener.onDoneMenuClick(it, done, position)
            }
        }
    }

    private class DoneComparator: DiffUtil.ItemCallback<Done>() {
        override fun areItemsTheSame(oldItem: Done, newItem: Done): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Done, newItem: Done): Boolean {
            return oldItem.doneId == newItem.doneId
        }
    }

    interface OnDoneMenuClickListener {
        fun onDoneMenuClick(v: View, done: Done, position: Int)
    }

    fun setDoneMenuClickListener(onDoneMenuClickListener: OnDoneMenuClickListener) {
        this.doneMenuClickListener = onDoneMenuClickListener
    }
}