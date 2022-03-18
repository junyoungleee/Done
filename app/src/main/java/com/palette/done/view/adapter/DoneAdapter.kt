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
import com.palette.done.data.db.entity.Done
import com.palette.done.databinding.ItemDoneBinding

/**
 * ActivityDone
 *  던리스트 Recyclerview Adapter
 */
class DoneAdapter(val context: Context): ListAdapter<Done, DoneAdapter.DoneViewHolder>(DoneComparator())  {

    private lateinit var doneClickListener: OnDoneClickListener

    class DoneViewHolder(val binding: ItemDoneBinding): RecyclerView.ViewHolder(binding.root) { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoneViewHolder {
        val binding = ItemDoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoneViewHolder, position: Int) {
        val done = getItem(position)
        with(holder.binding) {
            val category = done.categoryNo
            if (category != null) {
                val name = "ic_category_$category"
                val imgId = context.resources.getIdentifier(name, "drawable", context.packageName)
                ivDoneCategory.setImageDrawable(ContextCompat.getDrawable(context, imgId))
            } else {
                ivDoneCategory.setImageDrawable(null)
            }
            tvDoneContent.text = done.content
            tvDoneContent.setOnClickListener {
                doneClickListener.onDoneRootClick(it)
            }
            ivDoneCategory.setOnClickListener {
                doneClickListener.onDoneRootClick(it)
            }
            btnDoneMenu.setOnClickListener {
                doneClickListener.onDoneMenuClick(it, done, position)
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

    interface OnDoneClickListener {
        fun onDoneMenuClick(v: View, done: Done, position: Int)
        fun onDoneRootClick(v: View)
    }

    fun setDoneClickListener(onDoneClickListener: OnDoneClickListener) {
        this.doneClickListener = onDoneClickListener
    }
}