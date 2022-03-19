package com.palette.done.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.databinding.ItemLandingBinding
import com.palette.done.view.LandingItem

class LandingAdapter(private val list: ArrayList<LandingItem>): RecyclerView.Adapter<LandingAdapter.LandingViewHolder>() {

    class LandingViewHolder(val binding: ItemLandingBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandingViewHolder {
        val binding = ItemLandingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LandingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LandingViewHolder, position: Int) {
        with(holder.binding) {
            tvTitle.text = list[position].title
            tvDetail.text = list[position].detail
            ivDetailImage.setImageDrawable(list[position].image)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}