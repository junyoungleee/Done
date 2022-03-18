package com.palette.done.view.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.R
import com.palette.done.databinding.ItemObAlarmWeekBinding
import com.palette.done.view.util.Util

class ObAlarmRecyclerViewAdapter(var width: Int) : RecyclerView.Adapter<ObAlarmRecyclerViewAdapter.ObAlarmViewHolder>() {

    private val week = arrayListOf("월", "화", "수", "목", "금", "토", "일")
    private lateinit var weekItemClickListener: OnWeekItemClickListener
    private val util = Util()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObAlarmViewHolder {
        val binding = ItemObAlarmWeekBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemWidth = (width)/7
        binding.tvWeekDay.layoutParams = RecyclerView.LayoutParams(itemWidth, util.dpToPx(40))
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