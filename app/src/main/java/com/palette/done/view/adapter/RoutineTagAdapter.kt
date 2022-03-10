package com.palette.done.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.data.db.entity.Routine
import com.palette.done.databinding.ItemTagBinding

class RoutineTagAdapter : ListAdapter<Routine, RoutineTagAdapter.RoutineTagViewHolder>(RoutineComparator()){
    private lateinit var tagItemClickListener: OnTagItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineTagViewHolder {
        val binding = ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineTagAdapter.RoutineTagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineTagViewHolder, position: Int) {
        val routine = getItem(position)
        with(holder.binding) {
            tvTag.text = routine.content
            root.setOnClickListener {
                tagItemClickListener.onTagClick(it, routine)
            }
        }
    }

    class RoutineTagViewHolder(val binding: ItemTagBinding): RecyclerView.ViewHolder(binding.root) { }

    private class RoutineComparator: DiffUtil.ItemCallback<Routine>() {
        override fun areItemsTheSame(oldItem: Routine, newItem: Routine): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Routine, newItem: Routine): Boolean {
            return oldItem.routineNo == newItem.routineNo
        }
    }

    interface OnTagItemClickListener {
        fun onTagClick(v: View, routine: Routine)
    }

    fun setTagItemClickListener(onRoutineItemClickListener: OnTagItemClickListener) {
        this.tagItemClickListener = onRoutineItemClickListener
    }
}
