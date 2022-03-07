package com.palette.done.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.entity.Plan
import com.palette.done.databinding.ItemDoneBinding
import com.palette.done.databinding.ItemPlanRoutineBinding

class PlanAdapter : ListAdapter<Plan, PlanAdapter.PlanViewHolder>(PlanComparator())  {

    class PlanViewHolder(val binding: ItemPlanRoutineBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: Plan) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class PlanComparator: DiffUtil.ItemCallback<Plan>() {
        override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem.planNo == newItem.planNo
        }

    }
}