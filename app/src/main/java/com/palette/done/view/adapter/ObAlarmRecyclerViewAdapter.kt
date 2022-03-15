package com.palette.done.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.R
import com.palette.done.databinding.ItemObAlarmWeekBinding

class ObAlarmRecyclerViewAdapter : RecyclerView.Adapter<ObAlarmRecyclerViewAdapter.ObAlarmViewHolder>() {

    private val week = arrayListOf("월", "화", "수", "목", "금", "토", "일")
    private lateinit var weekItemClickListener: OnWeekItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObAlarmViewHolder {
        val binding = ItemObAlarmWeekBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ObAlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ObAlarmViewHolder, position: Int) {
        holder.binding.tvWeekDay.text = week[position]
        holder.binding.tvWeekDay.setOnClickListener {
            weekItemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return week.size
    }

    class ObAlarmViewHolder(val binding: ItemObAlarmWeekBinding): RecyclerView.ViewHolder(binding.root)

    interface OnWeekItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setWeekItemClickListener(onWeekItemClickListener: OnWeekItemClickListener) {
        this.weekItemClickListener = onWeekItemClickListener
    }
}